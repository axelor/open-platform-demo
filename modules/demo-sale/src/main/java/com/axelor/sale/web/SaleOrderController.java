/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2022 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.sale.web;

import com.axelor.common.ObjectUtils;
import com.axelor.db.JpaSupport;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.sale.db.Order;
import com.axelor.sale.db.OrderStatus;
import com.axelor.sale.service.SaleOrderService;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class SaleOrderController extends JpaSupport {

  @Inject private SaleOrderService service;

  public void onConfirm(ActionRequest request, ActionResponse response) {

    Order order = request.getContext().asType(Order.class);

    response.setReadonly("orderDate", order.getConfirmed());
    response.setReadonly("confirmDate", order.getConfirmed());

    if (order.getConfirmed() == Boolean.TRUE && order.getConfirmDate() == null) {
      response.setValue("confirmDate", LocalDate.now());
    }

    if (order.getConfirmed() == Boolean.TRUE) {
      response.setValue("status", OrderStatus.OPEN);
    } else if (order.getStatus() == OrderStatus.OPEN) {
      response.setValue("status", OrderStatus.DRAFT);
    }
  }

  public void calculate(ActionRequest request, ActionResponse response) {

    Order order = request.getContext().asType(Order.class);
    order = service.calculate(order);

    response.setValue("amount", order.getAmount());
    response.setValue("taxAmount", order.getTaxAmount());
    response.setValue("totalAmount", order.getTotalAmount());
  }

  public void reportToday(ActionRequest request, ActionResponse response) {
    EntityManager em = getEntityManager();
    Query q1 =
        em.createQuery(
            "SELECT SUM(self.totalAmount) FROM Order AS self "
                + "WHERE YEAR(self.orderDate) = YEAR(current_date) AND "
                + "MONTH(self.orderDate) = MONTH(current_date) AND "
                + "DAY(self.orderDate) = DAY(current_date) - 1");

    Query q2 =
        em.createQuery(
            "SELECT SUM(self.totalAmount) FROM Order AS self "
                + "WHERE YEAR(self.orderDate) = YEAR(current_date) AND "
                + "MONTH(self.orderDate) = MONTH(current_date) AND "
                + "DAY(self.orderDate) = DAY(current_date)");

    List<?> r1 = q1.getResultList();
    BigDecimal last = r1.get(0) == null ? BigDecimal.ZERO : (BigDecimal) r1.get(0);

    List<?> r2 = q2.getResultList();
    BigDecimal total = r2.get(0) == null ? BigDecimal.ZERO : (BigDecimal) r2.get(0);

    BigDecimal percent = BigDecimal.ZERO;
    if (total.compareTo(BigDecimal.ZERO) == 1) {
      percent =
          total.subtract(last).multiply(new BigDecimal(100)).divide(total, RoundingMode.HALF_UP);
    }

    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("percent", percent);
    data.put("down", total.compareTo(last) == -1);

    response.setData(Lists.newArrayList(data));
  }

  public void reportMonthly(ActionRequest request, ActionResponse response) {
    EntityManager em = getEntityManager();
    Query q1 =
        em.createQuery(
            "SELECT SUM(self.totalAmount) FROM Order AS self "
                + "WHERE YEAR(self.orderDate) = YEAR(current_date) AND "
                + "MONTH(self.orderDate) = MONTH(current_date) - 1");

    Query q2 =
        em.createQuery(
            "SELECT SUM(self.totalAmount) FROM Order AS self "
                + "WHERE YEAR(self.orderDate) = YEAR(current_date) AND "
                + "MONTH(self.orderDate) = MONTH(current_date)");

    List<?> r1 = q1.getResultList();
    BigDecimal last = r1.get(0) == null ? BigDecimal.ZERO : (BigDecimal) r1.get(0);

    List<?> r2 = q2.getResultList();
    BigDecimal total = r2.get(0) == null ? BigDecimal.ZERO : (BigDecimal) r2.get(0);

    BigDecimal percent = BigDecimal.ZERO;
    if (total.compareTo(BigDecimal.ZERO) == 1) {
      percent = total.subtract(last).divide(total, 4, RoundingMode.HALF_UP);
    }

    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("percent", percent);
    data.put("up", total.compareTo(last) > 0);
    data.put("tag", I18n.get("Monthly"));
    data.put("tagCss", "label-success");

    response.setData(Lists.newArrayList(data));
  }

  public void showTotalSales(ActionRequest request, ActionResponse response) {
    List<Map<String, Object>> data =
        (List<Map<String, Object>>) request.getRawContext().get("_data");
    if (ObjectUtils.isEmpty(data)) {
      response.setNotify(I18n.get("No sales"));
      return;
    }
    BigDecimal totalAmount =
        data.stream()
            .map(i -> i.get("amount").toString())
            .map(BigDecimal::new)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    response.setNotify(String.format("%s : %s", I18n.get("Total sales"), totalAmount));
  }

  public void showCustomerSales(ActionRequest request, ActionResponse response) {
    Object data = request.getRawContext().get("customerId");
    if (ObjectUtils.isEmpty(data)) {
      return;
    }

    ActionView.ActionViewBuilder builder =
        ActionView.define("Customer sales").model(Order.class.getName());
    builder.domain("self.customer.id = " + data);
    response.setView(builder.map());
  }
}

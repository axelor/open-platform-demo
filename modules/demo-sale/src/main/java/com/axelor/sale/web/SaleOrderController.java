/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2020 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.sale.web;

import com.axelor.db.JpaSupport;
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
      percent =
          total.subtract(last).multiply(new BigDecimal(100)).divide(total, RoundingMode.HALF_UP);
    }

    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("percent", percent);
    data.put("down", total.compareTo(last) == -1);

    response.setData(Lists.newArrayList(data));
  }
}

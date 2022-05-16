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
package com.axelor.sale.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.db.JpaSecurity;
import com.axelor.db.Query;
import com.axelor.inject.Beans;
import com.axelor.meta.CallMethod;
import com.axelor.sale.db.Order;
import com.axelor.sale.db.OrderStatus;
import java.util.Arrays;
import java.util.List;

public class SaleOrderTagsService {

  @CallMethod
  public String countOrdersMenuTag() {
    return countOrders(Arrays.asList(OrderStatus.values()));
  }

  @CallMethod
  public String countOrdersMenuTag(String status) {
    OrderStatus orderStatus = null;
    try {
      orderStatus = OrderStatus.valueOf(status);
    } catch (Exception e) {
      return null;
    }

    return countOrders(List.of(orderStatus));
  }

  private String countOrders(List<OrderStatus> status) {
    User user = AuthUtils.getUser();
    if (user == null
        || !Beans.get(JpaSecurity.class).isPermitted(JpaSecurity.CAN_READ, Order.class)) {
      return null;
    }
    return String.valueOf(Query.of(Order.class).filter("self.status IN (?1)", status).count());
  }
}

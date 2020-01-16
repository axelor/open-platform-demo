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
package com.axelor.data.tests;

import com.axelor.contact.db.Contact;
import com.axelor.db.JPA;
import com.axelor.sale.db.Order;
import com.axelor.sale.service.SaleOrderService;
import com.google.inject.Inject;
import java.util.Map;

public class Validators {

  @Inject private SaleOrderService soService;

  public Object validateSaleOrder(Object bean, Map<String, Object> context) {
    assert bean instanceof Order;
    Order so = (Order) bean;

    soService.validate(so);

    System.err.println("Date: " + so.getOrderDate());
    System.err.println("Customer: " + so.getCustomer().getFullName());
    System.err.println("Items: " + so.getItems().size());

    long count = JPA.all(Contact.class).count();
    assert count > 1;

    return bean;
  }
}

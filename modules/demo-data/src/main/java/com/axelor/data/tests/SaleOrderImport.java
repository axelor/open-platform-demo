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
package com.axelor.data.tests;

import com.axelor.contact.db.Contact;
import com.axelor.sale.db.Order;
import com.axelor.sale.db.OrderLine;
import java.time.LocalDate;
import java.util.Map;

public class SaleOrderImport {

  /**
   * This method is called with <code>prepare-context</code> attribute from the <code>&lt;input&gt;
   * </code> tag. It prepares the global context.
   */
  public void createOrder(Map<String, Object> context) {

    Order order = new Order();
    order.setOrderDate(LocalDate.now());

    context.put("_saleOrder", order);
  }

  /**
   * This method is called with <code>call</code> attribute from the <code>&lt;input&gt;</code> tag.
   *
   * <p>This method is called for each record being imported.
   *
   * @param bean the bean instance created from the imported record
   * @param values the value map that represents the imported data
   * @return the bean object to persist (in most cases the same bean object)
   */
  public Object updateOrder(Object bean, Map<String, Object> values) {

    assert bean instanceof OrderLine;
    assert values.get("_saleOrder") instanceof Order;
    assert values.get("_customer") instanceof Contact;

    Order so = (Order) values.get("_saleOrder");
    OrderLine line = (OrderLine) bean;
    Contact cust = (Contact) values.get("_customer");

    if (so.getCustomer() == null) {
      so.setCustomer(cust);
    }
    so.addItem(line);
    line.setOrder(so);

    return line;
  }
}

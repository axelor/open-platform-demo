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
package com.axelor.demo;

import com.axelor.sale.db.Order;
import com.axelor.sale.service.SaleOrderService;
import java.util.Map;
import javax.inject.Inject;

public class Validators {

  @Inject private SaleOrderService service;

  @SuppressWarnings("rawtypes")
  public Object validateSaleOrder(Object bean, Map context) {
    Order so = (Order) bean;

    service.validate(so);
    service.calculate(so);

    return so;
  }
}

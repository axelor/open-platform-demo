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
package com.axelor.demo;

import com.axelor.db.JpaSequence;
import com.axelor.sale.db.Order;
import com.axelor.sale.service.SaleOrderService;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.inject.Inject;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validators {

  private static final Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject private SaleOrderService service;

  @SuppressWarnings("rawtypes")
  public Object validateSaleOrder(Object bean, Map context) {
    Order so = (Order) bean;

    try {
      service.validate(so);
    } catch (ValidationException e) {
      logger.error("Validation error on SO '{}': {}", so.getName(), e.getMessage());
    }
    service.calculate(so);
    JpaSequence.nextValue("sale.order.seq");

    return so;
  }
}

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

import com.axelor.common.ObjectUtils;
import com.axelor.sale.db.Order;
import com.axelor.sale.db.OrderLine;
import com.axelor.sale.db.Tax;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.validation.ValidationException;

public class SaleOrderService {

  public void validate(Order order) {
    if (order != null
        && order.getConfirmDate() != null
        && order.getConfirmDate().isBefore(order.getOrderDate())) {
      throw new ValidationException("Invalid sale order, confirm date is before order date.");
    }
  }

  public Order calculate(Order order) {

    BigDecimal amount = BigDecimal.ZERO;
    BigDecimal taxAmount = BigDecimal.ZERO;

    if (!ObjectUtils.isEmpty(order.getItems())) {
      for (OrderLine item : order.getItems()) {
        BigDecimal value = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
        BigDecimal taxValue = BigDecimal.ZERO;

        if (!ObjectUtils.isEmpty(item.getTaxes())) {
          for (Tax tax : item.getTaxes()) {
            taxValue = taxValue.add(tax.getRate().multiply(value));
          }
        }

        amount = amount.add(value);
        taxAmount = taxAmount.add(taxValue);
      }
    }

    order.setAmount(amount.setScale(4, RoundingMode.HALF_UP));
    order.setTaxAmount(taxAmount.setScale(4, RoundingMode.HALF_UP));
    order.setTotalAmount(amount.add(taxAmount).setScale(4, RoundingMode.HALF_UP));

    return order;
  }
}

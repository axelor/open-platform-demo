/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2014 Axelor (<http://axelor.com>).
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
package com.axelor.sale.service

import java.math.RoundingMode;

import javax.validation.ValidationException;

import com.axelor.db.mapper.Adapter;
import com.axelor.db.mapper.Mapper;
import com.axelor.rpc.ActionRequest
import com.axelor.rpc.ActionResponse
import com.axelor.sale.db.Order

class SaleOrderService {
	
	void validate(Order order) {
		if (order?.confirmDate?.isBefore(order?.createDate)) {
			throw new ValidationException("invalid sale order, confirm date is in future")
		}
	}
	
	Order calculate(Order order) {
		
		if (!order.items || order.items.empty)
			return order;
		
		BigDecimal amount = order.items.collect { it.quantity * it.price }.sum()
		BigDecimal taxAmount = order.items.collect {
			def value = it.quantity * it.price;
			it.taxes ? it.taxes.collect { value * it.rate }.sum() : 0
		}.sum()

		BigDecimal totalAmount = amount + taxAmount

		order.amount = amount.setScale(4, RoundingMode.HALF_UP)
		order.taxAmount = taxAmount.setScale(4, RoundingMode.HALF_UP)
		order.totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP)
		
		return order
	}
}

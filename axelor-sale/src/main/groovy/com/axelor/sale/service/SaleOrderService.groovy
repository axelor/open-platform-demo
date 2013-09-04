/**
 * Copyright (c) 2012-2013 Axelor. All Rights Reserved.
 *
 * The contents of this file are subject to the Common Public
 * Attribution License Version 1.0 (the “License”); you may not use
 * this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://license.axelor.com/.
 *
 * The License is based on the Mozilla Public License Version 1.1 but
 * Sections 14 and 15 have been added to cover use of software over a
 * computer network and provide for limited attribution for the
 * Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 *
 * Software distributed under the License is distributed on an “AS IS”
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is part of "Axelor Business Suite", developed by
 * Axelor exclusively.
 *
 * The Original Developer is the Initial Developer. The Initial Developer of
 * the Original Code is Axelor.
 *
 * All portions of the code written by Axelor are
 * Copyright (c) 2012-2013 Axelor. All Rights Reserved.
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

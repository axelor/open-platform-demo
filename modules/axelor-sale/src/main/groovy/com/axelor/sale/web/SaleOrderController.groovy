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
package com.axelor.sale.web

import javax.inject.Inject

import org.joda.time.LocalDate

import com.axelor.rpc.ActionRequest
import com.axelor.rpc.ActionResponse
import com.axelor.sale.db.Order
import com.axelor.sale.service.SaleOrderService

class SaleOrderController {
	
	@Inject
	SaleOrderService service

	void onConfirm(ActionRequest request, ActionResponse response) {
		
		def context = request.context as Order
		def readonly = context.confirmed ? true : false
		
		response.setReadonly("orderDate", readonly)
		response.setReadonly("createDate", readonly)
		response.setReadonly("confirmDate", readonly)
		
		if (context.confirmed && !context.confirmDate) {
			def now = new LocalDate()
			response.setValues([
				confirmDate : now
			])
		}
	}
	
	void calculate(ActionRequest request, ActionResponse response) {
		
		def order = request.context as Order
		order = service.calculate(order)
		
		response.setValues([
			amount : order.amount,
			taxAmount : order.taxAmount,
			totalAmount : order.totalAmount
		])
	}
}

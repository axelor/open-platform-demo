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

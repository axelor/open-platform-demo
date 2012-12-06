package com.axelor.sale.service

import com.axelor.rpc.ActionRequest
import com.axelor.rpc.ActionResponse
import com.axelor.sale.db.Order

class SaleOrderService {
	
	void validate(Order order) {
		assert order?.confirmDate.isAfter(order?.createDate)
	}
	
	Order calculate(Order order) {
		
		if (!order.items || order.items.empty)
			return order;
		
		def amount = order.items.collect { it.quantity * it.price }.sum()
		def taxAmount = order.items.collect {
			def value = it.quantity * it.price;
			it.taxes ? it.taxes.collect { value * it.rate }.sum() : 0
		}.sum()

		def totalAmount = amount + taxAmount
		
		order.amount = amount
		order.taxAmount = taxAmount
		order.totalAmount = totalAmount
		
		return order
	}
}

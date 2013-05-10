package com.axelor.app

import com.axelor.sale.db.Order
import com.axelor.sale.service.SaleOrderService
import com.google.inject.Inject

class Validators {

	@Inject
	SaleOrderService service

	Object validateSaleOrder(Object bean, Map context) {
		assert bean instanceof Order
		Order so = (Order) bean

		service.validate(so)
		service.calculate(so);

		return bean
	}
}

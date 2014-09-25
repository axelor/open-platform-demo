/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2012-2014 Axelor (<http://axelor.com>).
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
package com.axelor.data.tests

import com.google.inject.Inject
import com.google.inject.Injector

import com.axelor.db.JPA
import com.axelor.contact.db.Contact
import com.axelor.sale.service.SaleOrderService
import com.axelor.sale.db.Order

class Validators {

	@Inject
	SaleOrderService soService

	Object validateSaleOrder(Object bean, Map context) {
		assert bean instanceof Order
		Order so = (Order) bean

		soService.validate(so)

		println("Date: $so.orderDate")
		println("Customer: $so.customer.firstName $so.customer.lastName")
		println("Items: $so.items.size")

		int count = JPA.all(Contact.class).count()
		assert count > 1

		return bean
	}
}

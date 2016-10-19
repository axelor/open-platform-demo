/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2016 Axelor (<http://axelor.com>).
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

import com.axelor.db.JpaSupport
import com.axelor.rpc.ActionRequest
import com.axelor.rpc.ActionResponse
import com.axelor.sale.db.Order
import com.axelor.sale.service.SaleOrderService

class SaleOrderController extends JpaSupport {

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

	void reportToday(ActionRequest request, ActionResponse response) {
		def em = getEntityManager()
		def q1 = em.createQuery(
			"SELECT SUM(self.totalAmount) FROM Order AS self " +
			"WHERE YEAR(self.orderDate) = YEAR(current_date) AND " +
			"MONTH(self.orderDate) = MONTH(current_date) AND " +
			"DAY(self.orderDate) = DAY(current_date) - 1")

		def q2 = em.createQuery(
			"SELECT SUM(self.totalAmount) FROM Order AS self " +
			"WHERE YEAR(self.orderDate) = YEAR(current_date) AND " +
			"MONTH(self.orderDate) = MONTH(current_date) AND " +
			"DAY(self.orderDate) = DAY(current_date)")

		def last = q1.getResultList().last() ?: 0
		def total = q2.getResultList().last() ?: 0

		def percent = 0
		if (total > 0) {
			percent = ((total - last) * 100) / total
		}

		response.data = [
			[total: total, percent: percent, down: total < last]
		]
	}

	void reportMonthly(ActionRequest request, ActionResponse response) {
		def em = getEntityManager()
		def q1 = em.createQuery(
			"SELECT SUM(self.totalAmount) FROM Order AS self " +
			"WHERE YEAR(self.orderDate) = YEAR(current_date) AND " +
			"MONTH(self.orderDate) = MONTH(current_date) - 1")

		def q2 = em.createQuery(
			"SELECT SUM(self.totalAmount) FROM Order AS self " +
			"WHERE YEAR(self.orderDate) = YEAR(current_date) AND " +
			"MONTH(self.orderDate) = MONTH(current_date)")

		def last = q1.getResultList().last() ?: 0
		def total = q2.getResultList().last() ?: 0

		def percent = 0
		if (total > 0) {
			percent = ((total - last) * 100) / total
		}

		response.data = [
			[total: total, percent: percent, down: total < last]
		]
	}
}

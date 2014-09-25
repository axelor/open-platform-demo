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
package com.axelor.sale

import groovy.transform.CompileStatic

import javax.inject.Inject

import org.joda.time.LocalDate
import org.junit.Assert;
import org.junit.Test
import org.junit.runner.RunWith

import com.axelor.contact.db.Address
import com.axelor.contact.db.Circle
import com.axelor.contact.db.Contact
import com.axelor.contact.db.Country
import com.axelor.contact.db.Email
import com.axelor.contact.db.Title
import com.axelor.db.JpaSupport
import com.axelor.sale.db.Order
import com.axelor.sale.db.OrderLine
import com.axelor.sale.db.Product
import com.axelor.sale.db.repo.OrderRepository
import com.axelor.test.GuiceModules
import com.axelor.test.GuiceRunner
import com.google.inject.persist.Transactional

@RunWith(GuiceRunner.class)
@GuiceModules([ TestModule.class ])
@CompileStatic
class GroovySaleTest extends JpaSupport {

	@Inject
	OrderRepository orders

	final long MAX_COUNT = 100L

	Order createSaleOrder() {

		def random = new Random()
		def next = random.nextInt()

		def title = new Title(
			code: "mr_" + next,
			name: "Mr_" + next)
		
		def circle = new Circle(
			code: "circle" + next,
			name: "Circle_" + next)

		def country = new Country(
			code: "country_" + next,
			name: "country_" + next)
		
		def contact = new Contact(
			title: title,
			firstName: "FirstName_" + next,
			lastName: "LastName_" + next)

		contact.addEmail(new Email(email: "first.last.${next}@gmail.com"));
		contact.addCircle(circle);

		def addr1 = new Address(
			street: "My",
			area: "Home",
			city: "Paris",
			country: country);
		
		def addr2 = new Address(
			street: "My",
			area: "Office",
			city: "Paris",
			country: country);
		
		contact.addAddress(addr1)
		contact.addAddress(addr2)
		
		def p1 = new Product(
			code: "pc_" + next,
			name: "PC_" + next)

		next = random.nextInt();

		def p2 = new Product(
			code: "pc_" + next,
			name: "PC_" + next)
		
		def order = new Order(
			customer: contact,
			orderDate: new LocalDate(),
			createDate: new LocalDate())
		
		def item1 = new OrderLine(
			product: p1,
			price: new BigDecimal("250.23"),
			quantity: random.nextInt(20) + 1)
		
		def item2 = new OrderLine(
			product: p2,
			price: new BigDecimal("934.33"),
			quantity: random.nextInt(20) + 1)

		order.addItem(item1)
		order.addItem(item2)
		
		return order
	}

	@Transactional
	void createData() {
		for(i in 0..<MAX_COUNT) {
			orders.save(createSaleOrder())
		}
	}

	@Transactional
	void dropData() {
		all(OrderLine).delete();
		all(Order).delete();
		all(Product).delete();
		all(Address).delete();
		all(Email).delete();
		all(Contact).delete();
		all(Country).delete();
		all(Circle).delete();
		all(Title).delete();
	}

	void listData() {
		System.err.println("Title: " + all(Title).count());
		System.err.println("Circle: " + all(Circle).count());
		System.err.println("Country: " + all(Country).count());
		System.err.println("Contact: " + all(Contact).count());
		System.err.println("Address: " + all(Address).count());
		System.err.println("Product: " + all(Product).count());
		System.err.println("Order: " + all(Order).count());
		System.err.println("OrderLine: " + all(OrderLine).count());
	}

	@Test
	public void test() {
		createData();
		Assert.assertEquals(MAX_COUNT, all(Order).count());

		dropData();
		Assert.assertEquals(0L, all(Order).count());
	}
}

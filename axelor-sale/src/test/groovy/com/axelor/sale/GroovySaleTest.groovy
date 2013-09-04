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
package com.axelor.sale

import java.util.Random

import org.joda.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith

import com.axelor.contact.db.Address
import com.axelor.contact.db.Contact
import com.axelor.contact.db.Country
import com.axelor.contact.db.Circle
import com.axelor.contact.db.Title
import com.axelor.db.JPA;
import com.axelor.db.Model
import com.axelor.sale.db.Order
import com.axelor.sale.db.OrderLine
import com.axelor.sale.db.Product
import com.axelor.test.GuiceModules
import com.axelor.test.GuiceRunner
import com.google.inject.persist.Transactional;

@RunWith(GuiceRunner.class)
@GuiceModules([ TestModule.class ])
class GroovySaleTest {

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
			lastName: "LastName_" + next,
			email: "first.last.${next}@gmail.com",
			circle: circle)
		
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
		
		contact.addresses = [addr1, addr2]
		
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

		order.items = [item1, item2]
		
		return order
	}

	@Transactional
	void createData() {
		for(i in 0..<1000) {
			JPA.manage(createSaleOrder())
		}
	}

	@Transactional
	void dropData() {
		OrderLine.all().delete();
		Order.all().delete();
		Product.all().delete();
		Address.all().delete();
		Contact.all().delete();
		Country.all().delete();
		Circle.all().delete();
		Title.all().delete();
	}

	void listData() {
		System.err.println("Title: " + Title.all().count());
		System.err.println("Circle: " + Circle.all().count());
		System.err.println("Country: " + Country.all().count());
		System.err.println("Contact: " + Contact.all().count());
		System.err.println("Address: " + Address.all().count());
		System.err.println("Product: " + Product.all().count());
		System.err.println("Order: " + Order.all().count());
		System.err.println("OrderLine: " + OrderLine.all().count());
	}

	@Test
	public void test() {
		System.err.println("=======|| Create Random Records ||======");
		createData();
		listData();
		System.err.println("=======|| Delete Random Records ||======");
		dropData();
		listData();
	}
}

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
package com.axelor.sale;

import java.math.BigDecimal;
import java.util.Random;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.axelor.contact.db.Address;
import com.axelor.contact.db.Circle;
import com.axelor.contact.db.Contact;
import com.axelor.contact.db.Country;
import com.axelor.contact.db.Email;
import com.axelor.contact.db.Title;
import com.axelor.db.JPA;
import com.axelor.db.Model;
import com.axelor.db.Query;
import com.axelor.sale.db.Order;
import com.axelor.sale.db.OrderLine;
import com.axelor.sale.db.Product;
import com.axelor.test.GuiceModules;
import com.axelor.test.GuiceRunner;
import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;

@RunWith(GuiceRunner.class)
@GuiceModules({ TestModule.class })
public class SaleTest {

	Order createSaleOrder() {

		Random random = new Random();

		int next = random.nextInt();

		Title title = new Title();
		title.setCode("mr_" + next);
		title.setName("Mr_" + next);

		Circle circle = new Circle();
		circle.setCode("group_" + next);
		circle.setName("Group_" + next);

		Country country = new Country();
		country.setCode("country_" + next);
		country.setName("country_" + next);
		
		Email email = new Email();
		email.setEmail(String.format("first.last.%s@gmail.com", next));
		
		Contact contact = new Contact();
		contact.setTitle(title);
		contact.setFirstName("FirstName_" + next);
		contact.setLastName("LastName_" + next);
		contact.addEmail(email);
		contact.addCircle(circle);

		Address addr1 = new Address();
		addr1.setStreet("My");
		addr1.setArea("Home");
		addr1.setCity("Paris");
		addr1.setCountry(country);

		Address addr2 = new Address();
		addr2.setStreet("My");
		addr2.setArea("Home");
		addr2.setCity("Paris");
		addr2.setCountry(country);

		contact.setAddresses(Lists.newArrayList(addr1, addr2));

		Product p1 = new Product();
		p1.setCode("pc_" + next);
		p1.setName("PC_" + next);

		next = random.nextInt();
		
		Product p2 = new Product();
		p2.setCode("pc_" + next);
		p2.setName("PC_" + next);

		Order order = new Order();
		order.setCustomer(contact);
		order.setOrderDate(new LocalDate());
		order.setCreateDate(new LocalDate());

		OrderLine item1 = new OrderLine();
		item1.setProduct(p1);
		item1.setPrice(new BigDecimal("250.23"));
		item1.setQuantity(random.nextInt(20) + 1);

		OrderLine item2 = new OrderLine();
		item2.setProduct(p2);
		item2.setPrice(new BigDecimal("934.33"));
		item2.setQuantity(random.nextInt(20) + 1);
		
		order.setItems(Lists.newArrayList(item1, item2));

		return order;
	}

	@Transactional
	void createData() {
		int i = 0;
		while (i++ < 1000) {
			JPA.manage(createSaleOrder());
		}
	}

	private <T extends Model> Query<T> all(Class<T> klass) {
		return Query.of(klass);
	}

	@Transactional
	void dropData() {
		all(OrderLine.class).delete();
		all(Order.class).delete();
		all(Product.class).delete();
		all(Address.class).delete();
		all(Contact.class).delete();
		all(Country.class).delete();
		all(Circle.class).delete();
		all(Title.class).delete();
	}

	void listData() {
		System.err.println("Title: " + all(Title.class).count());
		System.err.println("Circle: " + all(Circle.class).count());
		System.err.println("Country: " + all(Country.class).count());
		System.err.println("Contact: " + all(Contact.class).count());
		System.err.println("Address: " + all(Address.class).count());
		System.err.println("Product: " + all(Product.class).count());
		System.err.println("Order: " + all(Order.class).count());
		System.err.println("OrderLine: " + all(OrderLine.class).count());
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

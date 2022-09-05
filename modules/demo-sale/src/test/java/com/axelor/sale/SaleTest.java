/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2022 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.sale;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.axelor.contact.db.Address;
import com.axelor.contact.db.Circle;
import com.axelor.contact.db.Contact;
import com.axelor.contact.db.Country;
import com.axelor.contact.db.Email;
import com.axelor.contact.db.Title;
import com.axelor.db.JPA;
import com.axelor.db.JpaSupport;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaSequence;
import com.axelor.meta.db.repo.MetaSequenceRepository;
import com.axelor.sale.db.Currency;
import com.axelor.sale.db.Order;
import com.axelor.sale.db.OrderLine;
import com.axelor.sale.db.Product;
import com.axelor.test.GuiceExtension;
import com.axelor.test.GuiceModules;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GuiceExtension.class)
@GuiceModules({TestModule.class})
public class SaleTest extends JpaSupport {

  static final String SEQUENCE_NAME = "sale.order.seq";
  static final long MAX_COUNT = 100L;

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

    contact.addAddress(addr1);
    contact.addAddress(addr2);

    Product p1 = new Product();
    p1.setCode("pc_" + next);
    p1.setName("PC_" + next);

    next = random.nextInt();

    Product p2 = new Product();
    p2.setCode("pc_" + next);
    p2.setName("PC_" + next);

    Currency currency = new Currency();
    currency.setCode("currency_" + next);
    currency.setName("CURRENCY_" + next);
    currency.setSymbol("C" + next);

    Order order = new Order();
    order.setCustomer(contact);
    order.setOrderDate(LocalDate.now());
    order.setCurrency(currency);

    OrderLine item1 = new OrderLine();
    item1.setProduct(p1);
    item1.setPrice(new BigDecimal("250.23"));
    item1.setQuantity(random.nextInt(20) + 1);

    OrderLine item2 = new OrderLine();
    item2.setProduct(p2);
    item2.setPrice(new BigDecimal("934.33"));
    item2.setQuantity(random.nextInt(20) + 1);

    order.addItem(item1);
    order.addItem(item2);

    return order;
  }

  @Transactional
  void createData() {
    int i = 0;
    while (i++ < MAX_COUNT) {
      JPA.manage(createSaleOrder());
    }
  }

  @Transactional
  void dropData() {
    all(OrderLine.class).delete();
    all(Order.class).delete();
    all(Product.class).delete();
    all(Address.class).delete();
    all(Email.class).delete();
    all(Contact.class).delete();
    all(Country.class).delete();
    all(Circle.class).delete();
    all(Title.class).delete();
    all(Currency.class).delete();
  }

  @Transactional
  void createSequence() {
    MetaSequence sequence = new MetaSequence(SEQUENCE_NAME);
    sequence.setInitial(1L);
    sequence.setIncrement(1);
    sequence.setPadding(5);
    sequence.setPrefix("SO");
    Beans.get(MetaSequenceRepository.class).save(sequence);
  }

  @Test
  public void test() {
    if (Beans.get(MetaSequenceRepository.class).findByName(SEQUENCE_NAME) == null) {
      createSequence();
    }

    createData();
    assertEquals(MAX_COUNT, all(Order.class).count());

    dropData();
    assertEquals(0L, all(Order.class).count());
  }
}

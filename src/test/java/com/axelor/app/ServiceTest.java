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
package com.axelor.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.axelor.contact.db.Contact;
import com.axelor.contact.service.HelloService;
import com.axelor.test.GuiceExtension;
import com.axelor.test.GuiceModules;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GuiceExtension.class)
@GuiceModules({MyModule.class})
public class ServiceTest {

  @Inject private HelloService service;

  @Test
  public void test() {

    Contact contact = new Contact();
    contact.setFirstName("John");
    contact.setLastName("Smith");

    String said = service.say(contact);
    String hello = service.hello();

    assertEquals("You are welcome 'John Smith!'", said);
    assertEquals("Hello world!!!", hello);
  }
}

/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2020 Axelor (<http://axelor.com>).
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
package com.axelor.sale.service;

import com.axelor.contact.db.Contact;
import com.axelor.contact.service.HelloServiceImpl;

public class HelloServiceSaleImpl extends HelloServiceImpl {

  @Override
  public String say(Contact contact) {
    log.info("Overrding the default HelloService.say ...");
    String message = super.say(contact);
    log.info("The default message was: {}", message);
    message = String.format("You are welcome '%s!'", contact.getFullName());
    log.info("I would say: {}", message);
    return message;
  }
}

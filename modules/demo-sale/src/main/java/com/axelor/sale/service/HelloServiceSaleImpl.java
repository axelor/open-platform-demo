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

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
package com.axelor.contact.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.contact.db.Contact;
import com.axelor.db.JpaSecurity;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.ui.QuickMenu;
import com.axelor.ui.QuickMenuCreator;
import com.axelor.ui.QuickMenuItem;
import java.util.ArrayList;

public class AccessContactQuickMenu implements QuickMenuCreator {

  @Override
  public QuickMenu create() {
    User user = AuthUtils.getUser();
    if (user == null
        || !Beans.get(JpaSecurity.class).isPermitted(JpaSecurity.CAN_READ, Contact.class)) {
      return null;
    }

    QuickMenu menu = createAccessQuickMenu();
    menu.getItems().add(new QuickMenuItem(I18n.get("Contacts"), "contact.all"));

    return menu;
  }

  protected QuickMenu createAccessQuickMenu() {
    return new QuickMenu(I18n.get("Access"), 10, new ArrayList<>());
  }
}

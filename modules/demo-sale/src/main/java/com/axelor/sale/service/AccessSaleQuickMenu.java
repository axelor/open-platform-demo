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

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.contact.service.AccessContactQuickMenu;
import com.axelor.db.JpaSecurity;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.sale.db.Order;
import com.axelor.ui.QuickMenu;
import com.axelor.ui.QuickMenuItem;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccessSaleQuickMenu extends AccessContactQuickMenu {

  @Override
  public QuickMenu create() {
    QuickMenu menu = super.create();

    User user = AuthUtils.getUser();
    if (user == null
        || !Beans.get(JpaSecurity.class).isPermitted(JpaSecurity.CAN_READ, Order.class)) {
      return menu;
    }

    if (menu == null) {
      // can be null in case use doesn't have permission on Contact domain.
      menu = createAccessQuickMenu();
    }

    menu.setItems(
        Stream.of(
                menu.getItems(),
                List.of(
                    new QuickMenuItem(I18n.get("Quotations"), "sale.orders.draft"),
                    new QuickMenuItem(I18n.get("Sale Orders"), "sale.orders")))
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));

    return menu;
  }
}

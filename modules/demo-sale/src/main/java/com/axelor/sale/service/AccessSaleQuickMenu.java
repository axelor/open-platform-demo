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

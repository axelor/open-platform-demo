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

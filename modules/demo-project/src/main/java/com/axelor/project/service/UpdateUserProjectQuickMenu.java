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
package com.axelor.project.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.db.JpaSecurity;
import com.axelor.db.Query;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.project.db.Project;
import com.axelor.project.web.ProjectController;
import com.axelor.rpc.Context;
import com.axelor.ui.QuickMenu;
import com.axelor.ui.QuickMenuCreator;
import com.axelor.ui.QuickMenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateUserProjectQuickMenu implements QuickMenuCreator {

  @Override
  public QuickMenu create() {
    return new QuickMenu(I18n.get("My projects"), 0, true, getItems());
  }

  public List<QuickMenuItem> getItems() {
    User user = AuthUtils.getUser();
    if (user == null
        || !Beans.get(JpaSecurity.class).isPermitted(JpaSecurity.CAN_READ, Project.class)) {
      return Collections.emptyList();
    }

    String action = ProjectController.class.getName() + ":updateDefaultProject";
    List<QuickMenuItem> items = new ArrayList<>();
    for (Project project :
        Query.of(Project.class).filter("?1 MEMBER OF self.members", user).fetch(10)) {
      boolean isDefaultProject = project.equals(user.getDefaultProject());
      QuickMenuItem item =
          new QuickMenuItem(
              project.getName(),
              action,
              new Context(project.getId(), Project.class),
              isDefaultProject);
      items.add(item);
    }
    return items;
  }
}

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
package com.axelor.project.web;

import com.axelor.auth.AuthUtils;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.project.db.Project;
import com.axelor.project.db.repo.ProjectRepository;
import com.axelor.project.service.UpdateUserProjectService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class ProjectController {

  public void updateDefaultProject(ActionRequest request, ActionResponse response) {
    Long projectId = (Long) request.getContext().get("id");

    Project project = Beans.get(ProjectRepository.class).find(projectId);
    if (project == null || AuthUtils.getUser() == null) {
      return;
    }

    Beans.get(UpdateUserProjectService.class).updateDefaultProject(AuthUtils.getUser(), project);

    response.setNotify(I18n.get("Default project updated."));
  }
}

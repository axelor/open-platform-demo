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

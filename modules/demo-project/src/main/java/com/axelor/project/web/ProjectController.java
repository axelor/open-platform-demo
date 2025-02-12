/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
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
import com.axelor.common.ObjectUtils;
import com.axelor.db.JPA;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.project.db.Project;
import com.axelor.project.db.ProjectTask;
import com.axelor.project.db.repo.ProjectRepository;
import com.axelor.project.service.ProjectTaskService;
import com.axelor.project.service.UpdateUserProjectService;
import com.axelor.project.util.TreeGridUtils;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectController {

  private static final String SUB_TASKS = "subTasks";

  private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

  public void updateDefaultProject(ActionRequest request, ActionResponse response) {
    Long projectId = (Long) request.getContext().get("id");

    Project project = Beans.get(ProjectRepository.class).find(projectId);
    if (project == null || AuthUtils.getUser() == null) {
      return;
    }

    Beans.get(UpdateUserProjectService.class).updateDefaultProject(AuthUtils.getUser(), project);

    response.setNotify(I18n.get("Default project updated."));
  }

  public void computeSubTasks(ActionRequest request, ActionResponse response) {
    var task = request.getContext().asType(ProjectTask.class);

    var service = Beans.get(ProjectTaskService.class);
    service.computeSubTasks(task);

    var resultMap = TreeGridUtils.toMap(task, SUB_TASKS);

    response.setValue(SUB_TASKS, resultMap.get(SUB_TASKS));
  }

  public void computeSubTasksTree(ActionRequest request, ActionResponse response) {
    var rawContext = request.getRawContext();

    @SuppressWarnings("unchecked")
    var subTasksMaps = (List<Map<String, Object>>) rawContext.get(SUB_TASKS);

    if (ObjectUtils.isEmpty(subTasksMaps)) {
      logger.info("No sub-tasks found");
      return;
    }

    var changedTaskMap = TreeGridUtils.findChangedItem(subTasksMaps, SUB_TASKS);

    if (changedTaskMap == null) {
      logger.info("No changed task found");
      return;
    }

    var changedFields = TreeGridUtils.getChangedFields(changedTaskMap);

    if (Stream.of("startDate", "endDate").noneMatch(changedFields::containsKey)) {
      logger.info("Dates not changed");
      return;
    }

    var changedTask = JPA.edit(ProjectTask.class, changedTaskMap);

    var service = Beans.get(ProjectTaskService.class);
    service.computeSubTasks(changedTask);

    var resultMap = TreeGridUtils.toMap(changedTask, SUB_TASKS);
    changedTaskMap.putAll(resultMap);

    response.setValue(SUB_TASKS, subTasksMaps);
  }
}

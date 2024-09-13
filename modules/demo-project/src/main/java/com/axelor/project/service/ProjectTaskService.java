/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
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

import com.axelor.common.ObjectUtils;
import com.axelor.project.db.ProjectTask;

public class ProjectTaskService {

  public void computeSubTasks(ProjectTask task) {
    var subTasks = task.getSubTasks();

    if (ObjectUtils.isEmpty(subTasks)) {
      return;
    }

    var startDate = task.getStartDate();
    var endDate = task.getEndDate();

    for (var subTask : task.getSubTasks()) {
      // Update start date
      if (startDate != null
          && (subTask.getStartDate() == null || subTask.getStartDate().isBefore(startDate))) {
        subTask.setStartDate(startDate);
      } else if (endDate != null && subTask.getStartDate().isAfter(endDate)) {
        subTask.setStartDate(endDate);
      }

      // Update end date
      if (endDate != null
          && (subTask.getEndDate() == null || subTask.getEndDate().isAfter(task.getEndDate()))) {
        subTask.setEndDate(endDate);
      } else if (startDate != null && subTask.getEndDate().isBefore(startDate)) {
        subTask.setEndDate(startDate);
      }

      // Recursively update sub-tasks of this task.
      computeSubTasks(subTask);
    }
  }
}

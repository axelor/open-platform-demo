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
package com.axelor.contact.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/** An example {@link Job} class that prints a some messages to the stderr. */
public class HelloJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDetail detail = context.getJobDetail();
    JobDataMap data = context.getJobDetail().getJobDataMap();

    String name = detail.getKey().getName();
    String desc = detail.getDescription();

    System.err.println("Job fired: " + name + " (" + desc + ")");
    if (data != null && data.size() > 0) {
      for (String key : data.keySet()) {
        System.err.println("    " + key + " = " + data.getString(key));
      }
    }
  }
}

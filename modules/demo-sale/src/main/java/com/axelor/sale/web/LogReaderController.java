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
package com.axelor.sale.web;

import com.axelor.db.JpaSupport;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.sale.db.LogReader;
import com.axelor.sale.db.repo.LogReaderRepository;
import com.axelor.sale.service.LogReaderService;
import java.util.List;
import java.util.stream.Collectors;

public class LogReaderController extends JpaSupport {

  public void setFileLog(ActionRequest request, ActionResponse response) {
    LogReader logReader = request.getContext().asType(LogReader.class);

    if (logReader.getId() == null) {
      logReader = Beans.get(LogReaderRepository.class).find(logReader.getId());
    }
    LogReaderService logReaderService = Beans.get(LogReaderService.class);
    String path = "/home/axelor/Documents/console.log";

    List<String> readLogReaderFilter =
        logReaderService.readLogReaderFilter(
            path,
            logReader.getNoOfLineToLoad(),
            logReader.getOffSet(),
            logReader.getStartDate(),
            logReader.getEndDate());

    int totalLines =
        logReaderService.countTotalLines(
            path,
            logReader.getNoOfLineToLoad(),
            logReader.getOffSet(),
            logReader.getStartDate(),
            logReader.getEndDate());
    
    String collect = readLogReaderFilter.stream().collect(Collectors.joining("\n"));
    String log =
        (logReader.getLog() == null && logReader.getOffSet() == null)
            ? readLogReaderFilter.stream().collect(Collectors.joining("\n"))
            : logReader.getLog();

    response.setValue("log", (log+ collect+"\n"));
    response.setValue("offSet", logReader.getOffSet() + logReader.getNoOfLineToLoad());

    if ((logReader.getOffSet() + logReader.getNoOfLineToLoad()) > totalLines) {
      response.setValue("offSet", 0);
      response.setNotify("No more log available");
    }
  }
}

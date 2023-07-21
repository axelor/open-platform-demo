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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogReaderServiceImpl implements LogReaderService {

  @Override
  public List<String> readLogReaderFilter(
      String path, int limit, int offset, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    List<String> filteredLogEntries = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {

      if (startDateTime != null && endDateTime != null) {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        filteredLogEntries =
            br.lines()
                .filter(
                    logLine -> {
                      Matcher matcher = pattern.matcher(logLine);
                      if (matcher.find()) {
                        String dateTimeStr = matcher.group();
                        LocalDateTime logDateTime =
                            LocalDateTime.parse(
                                dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return logDateTime.isAfter(startDateTime)
                            && logDateTime.isBefore(endDateTime);
                      }
                      return false;
                    })
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
      } else {
        filteredLogEntries = br.lines().skip(offset).limit(limit).collect(Collectors.toList());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return filteredLogEntries;
  }

  @Override
  public int countTotalLines(
      String path, int limit, int offset, LocalDateTime startDateTime, LocalDateTime endDateTime) {

    int count = 0;
    List<String> filteredLogEntries = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      if (startDateTime != null && endDateTime != null) {

        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        filteredLogEntries =
            br.lines()
                .filter(
                    logLine -> {
                      Matcher matcher = pattern.matcher(logLine);
                      if (matcher.find()) {
                        String dateTimeStr = matcher.group();
                        LocalDateTime logDateTime =
                            LocalDateTime.parse(
                                dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return logDateTime.isAfter(startDateTime)
                            && logDateTime.isBefore(endDateTime);
                      }
                      return false;
                    })
                .collect(Collectors.toList());

        count = filteredLogEntries.size();

      } else {
        filteredLogEntries = br.lines().collect(Collectors.toList());
        count = filteredLogEntries.size();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return count;
  }
}

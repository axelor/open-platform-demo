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
package com.axelor.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ImportUtils {

  public ImportUtils() {}

  public static Path findByFileName(Path path, String fileName) throws IOException {
    Path result = null;
    try (Stream<Path> pathStream =
        Files.find(
            path,
            Integer.MAX_VALUE,
            (p, basicFileAttributes) -> p.getFileName().toString().startsWith(fileName))) {
      result = pathStream.findFirst().orElse(null);
    }
    return result;
  }
}

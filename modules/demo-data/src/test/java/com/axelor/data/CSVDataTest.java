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
package com.axelor.data;

import com.axelor.data.csv.CSVImporter;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class CSVDataTest extends AbstractTest {

  @Test
  public void testDefault() throws IOException {
    Importer importer = new CSVImporter("data/csv-config.xml", "data/csv");
    importer.run();
  }

  @Test
  public void testMulti() throws IOException {
    Importer importer = new CSVImporter("data/csv-multi-config.xml");
    importer.run(
        new ImportTask() {
          @Override
          public void configure() throws IOException {
            input("[sale.order]", new File("data/csv-multi/so1.csv"));
            input("[sale.order]", new File("data/csv-multi/so2.csv"));
          }
        });
  }

  @Test
  public void testData() throws IOException {
    Importer importer = new CSVImporter("data/csv-config-types.xml", "data/csv");
    importer.run();
  }
}

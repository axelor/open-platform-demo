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

import com.axelor.data.xml.XMLImporter;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class XMLDataTest extends AbstractTest {

  @Test
  public void testTypes() throws IOException {
    Importer importer = new XMLImporter("data/xml-config-types.xml", "data/xml");
    importer.run();
  }

  @Test
  public void testDefault() throws IOException {
    Importer importer = new XMLImporter("data/xml-config.xml", "data/xml");
    importer.run();
  }
}

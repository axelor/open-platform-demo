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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class XMLImportTest extends AbstractTest {

  @Test
  public void test() throws FileNotFoundException {
    XMLImporter importer = new XMLImporter("data/xml-config.xml");
    Map<String, Object> context = new HashMap<>();

    context.put("LOCATION", "FR");
    context.put("DATE_FORMAT", "dd/MM/yyyy");

    importer.setContext(context);

    importer.run(
        new ImportTask() {

          @Override
          public void configure() throws IOException {
            input("contacts.xml", new File("data/xml/contacts.xml"));
            input(
                "contacts.xml",
                new File("data/xml/contacts-non-unicode.xml"),
                Charset.forName("ISO-8859-15"));
          }

          @Override
          public boolean handle(ImportException exception) {
            System.err.println("Import error: " + exception);
            return true;
          }
        });
  }
}

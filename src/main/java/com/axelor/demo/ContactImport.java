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

import com.axelor.contact.db.Contact;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ContactImport {

  private static final String CONTACT_IMAGES_DIR = "contact_images";

  public Object importContact(Object bean, Map context) {
    Contact contact = (Contact) bean;

    final Path path = (Path) context.get("__path__");

    try {
      final Path image =
          ImportUtils.findByFileName(
              path.resolve(CONTACT_IMAGES_DIR),
              String.join("-", contact.getFirstName(), contact.getLastName()));
      if (image != null && image.toFile().exists()) {
        contact.setImage(readFileToBytes(image));
      }
    } catch (Exception e) {
      // ignore
    }

    return contact;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private byte[] readFileToBytes(Path path) throws IOException {

    File file = path.toFile();
    byte[] bytes = new byte[(int) file.length()];

    try (FileInputStream fis = new FileInputStream(file)) {
      fis.read(bytes);
    }

    return bytes;
  }
}

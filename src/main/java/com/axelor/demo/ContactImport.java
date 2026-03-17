/*
 * SPDX-FileCopyrightText: Axelor <https://axelor.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
  private static final String CONTACT_IDDOCUMENTS_DIR = "contact_iddocuments";

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

    try {
      final Path doc =
          ImportUtils.findByFileName(
              path.resolve(CONTACT_IDDOCUMENTS_DIR),
              String.join("-", contact.getFirstName(), contact.getLastName()));
      if (doc != null && doc.toFile().exists()) {
        contact.setIdDocument(readFileToBytes(doc));
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

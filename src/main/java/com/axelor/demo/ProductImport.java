/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2023 Axelor (<http://axelor.com>).
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

import com.axelor.dms.db.DMSFile;
import com.axelor.dms.db.repo.DMSFileRepository;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.sale.db.Product;
import com.axelor.sale.db.repo.ProductRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ProductImport {

  private static final String PRODUCT_IMAGES_DIR = "product_images";
  private static final String PRODUCT_SHEETS_DIR = "product_sheets";

  public Object importProduct(Object bean, Map context) {
    Product product = (Product) bean;

    Beans.get(ProductRepository.class).save(product);

    loadImage(product, (Path) context.get("__path__"));
    loadAttachments(product, (Path) context.get("__path__"));
    loadProductSheet(product, (Path) context.get("__path__"));

    return product;
  }

  private void loadAttachments(Product product, Path basePath) {
    try {
      final Path attachment = ImportUtils.findByFileName(basePath.resolve("pdf"), "sample.pdf");
      if (attachment == null || !attachment.toFile().exists()) {
        return;
      }

      final MetaFile metaFile = new MetaFile();
      metaFile.setFileName("sample_" + product.getCode() + ".pdf");
      Beans.get(MetaFiles.class).upload(attachment.toFile(), metaFile);

      DMSFile dmsFile = new DMSFile();
      dmsFile.setMetaFile(metaFile);
      dmsFile.setFileName(metaFile.getFileName());
      dmsFile.setRelatedModel(Product.class.getName());
      dmsFile.setRelatedId(product.getId());
      Beans.get(DMSFileRepository.class).save(dmsFile);
    } catch (Exception e) {
      // ignore
    }
  }

  private void loadImage(Product product, Path basePath) {
    try {
      final Path image =
          ImportUtils.findByFileName(basePath.resolve(PRODUCT_IMAGES_DIR), product.getCode());
      if (image != null && image.toFile().exists()) {
        final MetaFile metaFile = Beans.get(MetaFiles.class).upload(image.toFile());
        product.setImage(metaFile);
      }
    } catch (Exception e) {
      // ignore
    }
  }

  private void loadProductSheet(Product product, Path basePath) {
    try {
      final Path productSheet =
          ImportUtils.findByFileName(basePath.resolve(PRODUCT_SHEETS_DIR), product.getCode());
      if (productSheet != null && productSheet.toFile().exists()) {
        product.setProductSheet(readFileToBytes(productSheet));
      }
    } catch (Exception e) {
      // ignore
    }
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

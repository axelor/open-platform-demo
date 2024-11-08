/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
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

import com.axelor.common.ObjectUtils;
import com.axelor.db.mapper.Adapter;
import com.axelor.dms.db.DMSFile;
import com.axelor.dms.db.repo.DMSFileRepository;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaJsonRecordRepository;
import com.axelor.sale.db.Product;
import com.axelor.sale.db.repo.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductImport {

  private static final String PRODUCT_IMAGES_DIR = "product_images";
  private static final String PRODUCT_SHEETS_DIR = "product_sheets";

  public Object importProduct(Object bean, Map context) {
    Product product = (Product) bean;

    Beans.get(ProductRepository.class).save(product);

    fillCustomFields(product, context);
    loadImage(product, (Path) context.get("__path__"));
    loadAttachments(product, (Path) context.get("__path__"));
    loadProductSheet(product, (Path) context.get("__path__"));

    return product;
  }

  private void fillCustomFields(Product product, Map context) {
    final Map<String, Object> map = new HashMap<>();

    if (ObjectUtils.notEmpty(context.get("attrs_weight"))) {
      map.put("weight", Adapter.adapt(context.get("attrs_weight"), BigDecimal.class, null, null));
    }
    if (ObjectUtils.notEmpty(context.get("attrs_dimensions"))) {
      map.put("dimensions", context.get("attrs_dimensions"));
    }
    if (ObjectUtils.notEmpty(context.get("attrs_brand"))) {
      map.put("brand", context.get("attrs_brand"));
    }
    if (ObjectUtils.notEmpty(context.get("attrs_extra_options"))) {
      List<Map<String, Object>> attrsExtraOptions =
          extraExtraOptions((String) context.get("attrs_extra_options"));
      if (ObjectUtils.notEmpty(attrsExtraOptions)) {
        map.put("extraOptions", attrsExtraOptions);
      }
    }

    if (ObjectUtils.isEmpty(map)) {
      return;
    }

    try {
      product.setAttrs(Beans.get(ObjectMapper.class).writeValueAsString(map));
    } catch (JsonProcessingException e) {
      // ignore
    }
  }

  private List<Map<String, Object>> extraExtraOptions(String attrsExtraOptions) {
    if (ObjectUtils.isEmpty(attrsExtraOptions)) {
      return null;
    }

    List<String> filters = new ArrayList<>();
    Map<String, Object> params = new HashMap<>();
    int count = 0;
    for (String importId : attrsExtraOptions.split("\\|")) {
      filters.add("self.attrs.importId = :import" + count);
      params.put("import" + count, importId);
      count++;
    }

    return Beans.get(MetaJsonRecordRepository.class)
        .all()
        .filter(String.join(" OR ", filters))
        .bind(params)
        .fetch()
        .stream()
        .map(
            metaJsonRecord -> {
              final Map<String, Object> map = new HashMap<>();
              map.put("id", metaJsonRecord.getId());
              return map;
            })
        .collect(Collectors.toList());
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

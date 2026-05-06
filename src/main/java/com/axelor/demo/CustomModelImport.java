/*
 * SPDX-FileCopyrightText: Axelor <https://axelor.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.axelor.demo;

import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.MetaJsonModel;
import com.axelor.meta.db.MetaJsonRecord;
import com.axelor.meta.db.repo.MetaJsonModelRepository;
import com.axelor.meta.db.repo.MetaJsonRecordRepository;
import com.axelor.rpc.Context;
import java.nio.file.Path;
import java.util.Map;

public class CustomModelImport {

  private static final String VEHICLE_IMAGES_DIR = "vehicle_images";

  public Object importCustomModel(Object bean, Map context) {
    return Beans.get(MetaJsonModelRepository.class).save((MetaJsonModel) bean);
  }

  public Object importProductExtraOptions(Object bean, Map context) {
    MetaJsonRecord record = (MetaJsonRecord) bean;
    MetaJsonRecordRepository repo = Beans.get(MetaJsonRecordRepository.class);

    Context extraOptions = repo.create(record);
    extraOptions.put("name", record.getName());
    extraOptions.put("importId", context.get("importId"));

    return repo.save(record);
  }

  public Object importVehicle(Object bean, Map context) {

    MetaJsonRecord record = (MetaJsonRecord) bean;
    MetaJsonRecordRepository repo = Beans.get(MetaJsonRecordRepository.class);
    Context vehicle = repo.create(record);

    try {
      String fileName = context.get("name").toString().replace(" ", "-").toLowerCase();
      final Path image =
          ImportUtils.findByFileName(
              ((Path) context.get("__path__")).resolve(VEHICLE_IMAGES_DIR), fileName);
      if (image != null && image.toFile().exists()) {
        final MetaFile metaFile = Beans.get(MetaFiles.class).upload(image.toFile());
        vehicle.put("image", metaFile);
      }
    } catch (Exception e) {
      // ignore
    }

    return repo.save(record);
  }

  public Object saveMetaJsonRecord(Object bean, Map context) {
    return Beans.get(MetaJsonRecordRepository.class).save((MetaJsonRecord) bean);
  }
}

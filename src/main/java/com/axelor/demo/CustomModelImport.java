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

import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaJsonModel;
import com.axelor.meta.db.MetaJsonRecord;
import com.axelor.meta.db.repo.MetaJsonModelRepository;
import com.axelor.meta.db.repo.MetaJsonRecordRepository;
import com.axelor.rpc.Context;
import java.util.Map;

public class CustomModelImport {

  public Object importCustomModel(Object bean, Map context) {
    MetaJsonModel model = (MetaJsonModel) bean;

    Beans.get(MetaJsonModelRepository.class).save(model);

    return model;
  }

  public Object importProductExtraOptions(Object bean, Map context) {
    MetaJsonRecord record = (MetaJsonRecord) bean;
    MetaJsonRecordRepository repo = Beans.get(MetaJsonRecordRepository.class);

    Context extraOptions = repo.create(record);
    extraOptions.put("name", record.getName());
    extraOptions.put("importId", context.get("importId"));

    return repo.save(record);
  }
}

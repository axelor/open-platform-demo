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
import com.axelor.contact.db.Company;
import com.axelor.contact.db.repo.CompanyRepository;
import java.nio.file.Path;
import java.util.Map;

public class CompanyImport {

  private static final String COMPANY_SIGNATURES_DIR = "company_signatures";

  public Object importCompany(Object bean, Map context) {
    Company company = (Company) bean;

    Beans.get(CompanyRepository.class).save(company);

    loadSignature(company, (Path) context.get("__path__"));

    return company;
  }

  private void loadSignature(Company company, Path basePath) {
    try {
      final Path signature =
          ImportUtils.findByFileName(basePath.resolve(COMPANY_SIGNATURES_DIR), company.getCode());
      if (signature != null && signature.toFile().exists()) {
        final MetaFile metaFile = Beans.get(MetaFiles.class).upload(signature.toFile());
        company.setSignature(metaFile);
      }
    } catch (Exception e) {
      // ignore
    }
  }
}

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

import com.axelor.db.EntityHelper;
import com.axelor.db.JPA;
import com.axelor.meta.db.MetaSequence;
import com.axelor.meta.db.repo.MetaSequenceRepository;
import com.axelor.test.GuiceExtension;
import com.axelor.test.GuiceModules;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(GuiceExtension.class)
@GuiceModules(DataModule.class)
public abstract class AbstractTest {

  protected final Logger log = LoggerFactory.getLogger(EntityHelper.getEntityClass(this));

  @Inject private MetaSequenceRepository sequences;

  private static boolean checked;

  @BeforeEach
  public void ensureSequence() {

    if (checked || sequences.findByName("sale.order.seq") != null) {
      return;
    }

    checked = true;

    final MetaSequence seq = new MetaSequence();
    seq.setName("sale.order.seq");
    seq.setPrefix("SO");
    seq.setPadding(5);

    JPA.runInTransaction(
        new Runnable() {

          @Override
          public void run() {
            sequences.save(seq);
          }
        });
  }
}

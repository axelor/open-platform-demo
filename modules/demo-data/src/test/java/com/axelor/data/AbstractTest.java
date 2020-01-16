/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2020 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.data;

import com.axelor.db.EntityHelper;
import com.axelor.db.JPA;
import com.axelor.meta.db.MetaSequence;
import com.axelor.meta.db.repo.MetaSequenceRepository;
import com.axelor.test.GuiceModules;
import com.axelor.test.GuiceRunner;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(GuiceRunner.class)
@GuiceModules(DataModule.class)
public abstract class AbstractTest {

  protected final Logger log = LoggerFactory.getLogger(EntityHelper.getEntityClass(this));

  @Inject private MetaSequenceRepository sequences;

  private static boolean checked;

  @Before
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

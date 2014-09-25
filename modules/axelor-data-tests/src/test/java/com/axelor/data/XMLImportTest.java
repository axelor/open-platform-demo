/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2012-2014 Axelor (<http://axelor.com>).
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.axelor.data.xml.XMLImporter;
import com.axelor.test.GuiceModules;
import com.axelor.test.GuiceRunner;
import com.google.common.collect.Maps;
import com.google.inject.Injector;

@RunWith(GuiceRunner.class)
@GuiceModules(DataModule.class)
public class XMLImportTest {

	@Inject
	private Injector injector;

	@Test
	public void test() throws FileNotFoundException {
		XMLImporter importer = new XMLImporter(injector, "data/xml-config.xml");
		Map<String, Object> context = Maps.newHashMap();

		context.put("LOCATION", "FR");
		context.put("DATE_FORMAT", "dd/MM/yyyy");

		importer.setContext(context);

		importer.runTask(new ImportTask(){

			@Override
			public void configure() throws IOException {
				input("contacts.xml", new File("data/xml/contacts.xml"));
				input("contacts.xml", new File("data/xml/contacts-non-unicode.xml"), Charset.forName("ISO-8859-15"));
			}

			@Override
			public boolean handle(ImportException exception) {
				System.err.println("Import error: " + exception);
				return true;
			}
		});
	}
}

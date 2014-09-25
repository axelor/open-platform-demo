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

import java.io.IOException;

import org.junit.Test;

import com.axelor.data.xml.XMLImporter;
import com.google.inject.AbstractModule;

public class XMLDataTest {

	static class Module extends DataModule {

		@Override
		protected void configureImport() {
			bind(Importer.class).to(XMLImporter.class);
		}
	}

	static class MyLauncher extends Launcher {

		@Override
		protected AbstractModule createModule() {

			return new Module();
		}
	}

	@Test
	public void testTypes() throws IOException {
		MyLauncher launcher = new MyLauncher();
		launcher.run("-c", "data/xml-config-types.xml", "-d", "data/xml");
	}

	@Test
	public void testDefault() throws IOException {
		MyLauncher launcher = new MyLauncher();
		launcher.run("-c", "data/xml-config.xml", "-d", "data/xml");
	}
}

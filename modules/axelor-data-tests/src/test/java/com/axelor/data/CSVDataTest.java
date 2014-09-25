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

import com.google.inject.AbstractModule;

public class CSVDataTest {

	static class MyLauncher extends Launcher {

		@Override
		protected AbstractModule createModule() {

			return new DataModule();
		}
	}

	@Test
	public void testDefault() throws IOException {
		MyLauncher launcher = new MyLauncher();
		launcher.run("-c", "data/csv-config.xml", "-d", "data/csv");
	}

	@Test
	public void testMulti() throws IOException {
		MyLauncher launcher = new MyLauncher();
		launcher.run("-c", "data/csv-multi-config.xml", "-d", "data/csv-multi", "-Dsale.order=so1.csv,so2.csv");
	}

	@Test
	public void testData() throws IOException {
		MyLauncher launcher = new MyLauncher();
		launcher.run("-c", "data/csv-config-types.xml", "-d", "data/csv");
	}
}

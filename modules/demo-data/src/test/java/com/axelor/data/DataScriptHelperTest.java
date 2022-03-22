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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Test the Groovy dynamic script evaluation. */
public class DataScriptHelperTest {

  private static final int MAX_COUNT = 1000;

  private DataScriptHelper helper = new DataScriptHelper(100, 1, false);

  @BeforeAll
  public static void doInit() {
    Binding binding = new Binding();
    binding.setVariable("count", 1000);
    new GroovyShell(binding).evaluate("x = count * 100");
  }

  @Test
  public void test_createShell() {
    int count = MAX_COUNT;
    while (--count > 0) {
      String expr = "count * " + (count % 100);
      Binding binding = new Binding();
      binding.setVariable("count", count);
      new GroovyShell(binding).evaluate(expr);
    }
  }

  @Test
  public void test_useCache() {
    int count = MAX_COUNT;
    while (--count > 0) {
      String expr = "count * " + (count % 100);
      Binding binding = new Binding();
      binding.setVariable("count", count);
      helper.eval(expr, binding);
    }
  }

  @Test
  public void test_single_createShell() {
    int count = MAX_COUNT;
    while (--count > 0) {
      String expr = "count * 100";
      Binding binding = new Binding();
      binding.setVariable("count", count);
      new GroovyShell(binding).evaluate(expr);
    }
  }

  @Test
  public void test_single_useCache() {
    int count = MAX_COUNT;
    while (--count > 0) {
      String expr = "count * 100";
      Binding binding = new Binding();
      binding.setVariable("count", count);
      helper.eval(expr, binding);
    }
  }
}

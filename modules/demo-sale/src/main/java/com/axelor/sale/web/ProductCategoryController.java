/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2026 Axelor (<http://axelor.com>).
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
package com.axelor.sale.web;

import com.axelor.db.JPA;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.sale.db.Product;
import com.axelor.sale.db.ProductCategory;
import java.util.List;

public class ProductCategoryController {

  public void displayTree(ActionRequest request, ActionResponse response) {
    var ctx = request.getContext();

    List<Object> ids = (List<Object>) ctx.get("_ids");
    if (ids == null || ids.isEmpty()) {
      response.setError("Please select one record");
      return;
    } else if (ids.size() > 1) {
      response.setError("Please select only one record");
      return;
    }

    var product = JPA.find(Product.class, Long.valueOf(ids.get(0).toString()));
    var category =
        JPA.find(ProductCategory.class, ctx.getParent().asType(ProductCategory.class).getId());

    StringBuilder tree = new StringBuilder(product.getName());
    do {
      tree.insert(0, category.getName() + " > ");
    } while ((category = category.getParent()) != null);

    response.setInfo(tree.toString());
  }
}

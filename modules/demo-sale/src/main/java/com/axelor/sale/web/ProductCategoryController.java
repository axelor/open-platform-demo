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

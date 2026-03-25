/*
 * SPDX-FileCopyrightText: Axelor <https://axelor.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.axelor.sale.web;

import com.axelor.db.JPA;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ProductController {

  public void fetchTotalSales(ActionRequest request, ActionResponse response) {
    long productId;
    try {
      productId = Long.parseLong(request.getContext().get("id").toString());
    } catch (Exception e) {
      return;
    }

    Long sales =
        JPA.em()
            .createQuery(
                "SELECT sum(self.quantity) "
                    + "FROM OrderLine self "
                    + "LEFT JOIN self.order as o "
                    + "LEFT JOIN self.product as p "
                    + "WHERE p.id = :productId AND o.confirmed IS TRUE AND YEAR(o.orderDate) = :year",
                Long.class)
            .setParameter("productId", productId)
            .setParameter("year", LocalDate.now().getYear())
            .getSingleResult();

    response.setValue("totalSales", sales == null ? 0 : sales);
  }

  public void fetchSummary(ActionRequest request, ActionResponse response) {
    var globalAvgPrice =
        JPA.em()
            .createQuery(
                """
                SELECT AVG(self.price)
                FROM Product self
                WHERE self.price IS NOT NULL
                """,
                Double.class)
            .getSingleResult();

    if (globalAvgPrice != null) {
      response.setValue(
          "globalAvgPrice", BigDecimal.valueOf(globalAvgPrice).setScale(2, RoundingMode.HALF_UP));
    }
  }
}

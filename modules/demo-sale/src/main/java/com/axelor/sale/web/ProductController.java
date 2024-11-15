/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
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
import java.time.LocalDate;

public class ProductController {

  public void fetchTotalSales(ActionRequest request, ActionResponse response) {
    Long productId;
    try {
      productId = Long.valueOf(request.getContext().get("id").toString());
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
}

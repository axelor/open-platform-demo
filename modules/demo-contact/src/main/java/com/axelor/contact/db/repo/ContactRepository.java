/*
 * SPDX-FileCopyrightText: Axelor <https://axelor.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.axelor.contact.db.repo;

import com.axelor.db.JPA;
import java.util.HashMap;
import java.util.Map;

public class ContactRepository extends AbstractContactRepository {

  @Override
  public Map<String, Object> populate(Map<String, Object> json, Map<String, Object> context) {
    if (!context.containsKey("json-enhance")) {
      return json;
    }
    try {
      Long id = (Long) json.get("id");
      var result =
          JPA.em()
              .createQuery(
                  """
                              SELECT CASE WHEN self.image IS NOT NULL THEN TRUE ELSE FALSE END as hasImage,
                              a.street as street, a.area as area, a.city as city, a.state as state, a.zip as zip, country.name as country
                              FROM Contact self
                              LEFT JOIN Address a ON a.contact = self AND a.id = (SELECT MIN(a2.id) FROM Address a2 WHERE a2.contact = self)
                              LEFT JOIN a.country country
                              WHERE self.id = :cId
                              """,
                  Object[].class)
              .setParameter("cId", id)
              .getResultStream()
              .findFirst()
              .orElse(null);
      if (result == null) {
        return json;
      }
      json.put("hasImage", result[0]);

      Map<Object, Object> map = new HashMap<>();
      map.put("street", result[1]);
      map.put("area", result[2]);
      map.put("city", result[3]);
      map.put("state", result[4]);
      map.put("zip", result[5]);
      map.put("country", result[6]);

      json.put("address", map);
    } catch (Exception e) {
      // ignore
    }

    return json;
  }
}

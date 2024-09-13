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
package com.axelor.project.util;

import com.axelor.common.ObjectUtils;
import com.axelor.db.Model;
import com.axelor.db.mapper.Mapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class TreeGridUtils {

  private TreeGridUtils() {}

  /**
   * Finds changed item in a tree-grid graph.
   *
   * @param items
   * @param fieldName
   * @return changed item
   */
  @Nullable
  public static Map<String, Object> findChangedItem(
      List<Map<String, Object>> items, String fieldName) {
    for (var item : items) {
      var changed = (Boolean) item.get("_changed");
      if (Boolean.TRUE.equals(changed)) {
        return item;
      }

      @SuppressWarnings("unchecked")
      var subItems = (List<Map<String, Object>>) item.get(fieldName);
      if (subItems != null) {
        var subItem = findChangedItem(subItems, fieldName);
        if (subItem != null) {
          return subItem;
        }
      }
    }
    return null;
  }

  /**
   * Finds changed item in a tree-grid graph.
   *
   * @param items
   * @param parent
   * @param fieldName
   * @return changed item and its parent
   */
  @Nullable
  public static ChangedResult findChangedItem(
      List<Map<String, Object>> items, Map<String, Object> parent, String fieldName) {
    for (var item : items) {
      var changed = (Boolean) item.get("_changed");
      if (Boolean.TRUE.equals(changed)) {
        return new ChangedResult(item, parent);
      }

      @SuppressWarnings("unchecked")
      var subItems = (List<Map<String, Object>>) item.get(fieldName);
      if (subItems != null) {
        var changedItem = findChangedItem(subItems, item, fieldName);
        if (changedItem != null) {
          return changedItem;
        }
      }
    }
    return null;
  }

  /**
   * Get changed fields for given tree-grid item
   *
   * @param item
   * @return map of field name -> previous value
   */
  public static Map<String, Object> getChangedFields(Map<String, Object> item) {
    Map<String, Object> changedFields = new HashMap<>();

    @SuppressWarnings("unchecked")
    var original = (Map<String, Object>) item.get("_original");

    if (original == null) {
      return changedFields;
    }

    for (var entry : item.entrySet()) {
      var key = entry.getKey();

      // Skip special fields
      if (List.of("_changed", "_original").contains(key)) {
        continue;
      }
      var currentValue = entry.getValue();

      var originalValue = original.get(key);

      if (!Objects.equals(currentValue, originalValue)) {
        changedFields.put(key, currentValue);
      }
    }

    return changedFields;
  }

  public static class ChangedResult {
    private final Map<String, Object> item;
    private final Map<String, Object> parent;

    public ChangedResult(Map<String, Object> item, Map<String, Object> parent) {
      this.item = item;
      this.parent = parent;
    }

    public Map<String, Object> getItem() {
      return item;
    }

    public Map<String, Object> getParent() {
      return parent;
    }
  }

  @Nullable
  public Map<String, Object> findItem(Map<String, Object> item, Long id, String fieldName) {
    // Check if the current items matches the ID.
    if (equalsId(item, id)) {
      return item;
    }

    // If not, search through sub-items.
    @SuppressWarnings("unchecked")
    var items = (List<Map<String, Object>>) item.get(fieldName);

    if (ObjectUtils.notEmpty(items)) {
      for (var subItems : items) {
        var found = findItem(subItems, id, fieldName);
        if (found != null) {
          return found;
        }
      }
    }

    return null;
  }

  public static Long getId(Map<String, Object> item) {
    return Optional.ofNullable(item.get("id"))
        .or(() -> Optional.ofNullable(item.get("cid")))
        .map(o -> (Number) o)
        .map(Number::longValue)
        .orElse(null);
  }

  private static boolean equalsId(Map<String, Object> item, Long id) {
    return Objects.equals(getId(item), id);
  }

  public static Map<String, Object> toMap(Model bean, String fieldName) {
    var map = Mapper.toMap(bean);

    @SuppressWarnings("unchecked")
    var subItems = (List<? extends Model>) map.get(fieldName);

    if (ObjectUtils.notEmpty(subItems)) {
      map.put(
          fieldName,
          subItems.stream().map(item -> toMap(item, fieldName)).collect(Collectors.toList()));
    }

    return map;
  }
}

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
package com.axelor.data.tests;

import com.axelor.db.JpaModel;
import com.google.common.base.MoreObjects;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DATA_TYPES")
public class Types extends JpaModel {

  private LocalDate date;

  private LocalTime time;

  private LocalDateTime dateTime;

  private ZonedDateTime dateTimeTz;

  private Boolean active;

  private Integer number;

  private BigDecimal price;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public ZonedDateTime getDateTimeTz() {
    return dateTimeTz;
  }

  public void setDateTimeTz(ZonedDateTime dateTimeTz) {
    this.dateTimeTz = dateTimeTz;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("date", date)
        .add("time", time)
        .add("datetime", dateTime)
        .add("datetime-tz", dateTimeTz)
        .add("active", active)
        .add("number", number)
        .add("price", price)
        .toString();
  }
}

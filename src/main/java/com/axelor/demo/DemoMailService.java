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
package com.axelor.demo;

import static com.axelor.common.StringUtils.isBlank;

import com.axelor.contact.db.Contact;
import com.axelor.contact.db.repo.ContactRepository;
import com.axelor.db.Model;
import com.axelor.db.Query;
import com.axelor.inject.Beans;
import com.axelor.mail.service.MailServiceImpl;
import com.google.common.base.Joiner;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import javax.mail.internet.InternetAddress;

@Singleton
public class DemoMailService extends MailServiceImpl {

  @Override
  public Model resolve(String email) {
    final ContactRepository contacts = Beans.get(ContactRepository.class);
    final Contact contact = contacts.findByEmail(email);
    if (contact != null) {
      return contact;
    }
    return super.resolve(email);
  }

  /** Override to return contact email addresses. */
  @Override
  public List<InternetAddress> findEmails(String matching, List<String> selected, int maxResult) {

    final List<String> where = new ArrayList<>();
    final Map<String, Object> params = new HashMap<>();

    where.add("self.email is not null");

    if (!isBlank(matching)) {
      where.add(
          "(LOWER(self.email) like LOWER(:email) OR LOWER(self.fullName) like LOWER(:email))");
      params.put("email", "%" + matching + "%");
    }
    if (selected != null && !selected.isEmpty()) {
      where.add("self.email not in (:selected)");
      params.put("selected", selected);
    }

    final String filter = Joiner.on(" AND ").join(where);
    final Query<Contact> query = Query.of(Contact.class);

    if (!isBlank(filter)) {
      query.filter(filter);
      query.bind(params);
    }

    final List<InternetAddress> addresses = new ArrayList<>();
    for (Contact contact : query.fetch(maxResult)) {
      try {
        final InternetAddress item = new InternetAddress(contact.getEmail(), contact.getFullName());
        addresses.add(item);
      } catch (UnsupportedEncodingException e) {
      }
    }

    return addresses;
  }
}

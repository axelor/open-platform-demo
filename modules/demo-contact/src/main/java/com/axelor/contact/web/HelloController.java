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
package com.axelor.contact.web;

import com.axelor.contact.db.Address;
import com.axelor.contact.db.Company;
import com.axelor.contact.db.Contact;
import com.axelor.contact.db.Country;
import com.axelor.contact.db.repo.CountryRepository;
import com.axelor.contact.service.HelloService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.meta.schema.actions.ActionView.ActionViewBuilder;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.base.Joiner;
import com.google.inject.persist.Transactional;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloController {

  protected Logger log = LoggerFactory.getLogger(getClass());

  @Inject private HelloService service;

  public void say(ActionRequest request, ActionResponse response) {

    Contact contact = request.getContext().asType(Contact.class);
    String message = service.say(contact);

    if (contact.getAddresses() != null) {
      int total = contact.getAddresses().size();
      int selected = 0;
      for (Address it : contact.getAddresses()) {
        if (it.isSelected()) {
          selected++;
        }
      }
      message +=
          "<br>"
              + String.format(
                  "You have selected %s record(s) from total %s address records.", selected, total);
    }

    log.info("send greetings to: ${contact.fullName}");

    response.setInfo(message, I18n.get("Greetings"));
  }

  public void about(ActionRequest request, ActionResponse response) {

    Address address = request.getContext().asType(Address.class);
    Contact contact = request.getContext().getParent().asType(Contact.class);

    String name = contact.getFullName();

    if (name == null) {
      name = contact.getFirstName();
    }

    String message = "Where are you from ?";
    if (address.getCountry() != null)
      message = String.format("'%s' is a beautiful country!", address.getCountry().getName());

    if (name != null) {
      message = String.format("Welcome '%s'...</br>", name) + message;
    }

    response.setInfo(message);
  }

  public void guessEmail(ActionRequest request, ActionResponse response) {

    Contact contact = request.getContext().asType(Contact.class);

    if (contact.getEmail() == null) {

      String email =
          Joiner.on(".").skipNulls().join(contact.getFirstName(), contact.getLastName())
              + "@gmail.com";
      List<Address> addresses = createAddresses();

      response.setValue("email", email.toLowerCase());
      response.setValue("addresses", addresses);
    }
  }

  private List<Address> createAddresses() {

    Country frCountry =
        Beans.get(CountryRepository.class).all().filter("self.code = ?", "FR").fetchOne();

    if (frCountry == null) {
      frCountry = createDefaultCountry();
    }

    return Arrays.asList(
        new Address("My", "Home", "Paris", frCountry),
        new Address("My", "Office", "Paris", frCountry));
  }

  @Transactional
  protected Country createDefaultCountry() {
    log.debug("creating a Country record: { code: \"FR\", name: \"France\"}");

    Country frCountry = new Country("FR", "France");
    return Beans.get(CountryRepository.class).save(frCountry);
  }

  public void showHomePage(ActionRequest request, ActionResponse response) {

    response.setView(ActionView.define("Axelor.com").add("html", "http://www.axelor.com/").map());
  }

  public void showCompanyList(ActionRequest request, ActionResponse response) {

    Contact contact = request.getContext().asType(Contact.class);
    ActionViewBuilder builder = ActionView.define("All Companies").model(Company.class.getName());

    if (contact.getCompany() != null) {
      builder.domain(
          "self.code = '"
              + contact.getCompany().getCode()
              + "' OR self.parent.code = '"
              + contact.getCompany().getCode()
              + "'");
    }

    response.setView(builder.map());
  }
}

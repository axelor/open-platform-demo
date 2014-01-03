/**
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
 *
 * The contents of this file are subject to the Common Public
 * Attribution License Version 1.0 (the “License”); you may not use
 * this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://license.axelor.com/.
 *
 * The License is based on the Mozilla Public License Version 1.1 but
 * Sections 14 and 15 have been added to cover use of software over a
 * computer network and provide for limited attribution for the
 * Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 *
 * Software distributed under the License is distributed on an “AS IS”
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is part of "Axelor Business Suite", developed by
 * Axelor exclusively.
 *
 * The Original Developer is the Initial Developer. The Initial Developer of
 * the Original Code is Axelor.
 *
 * All portions of the code written by Axelor are
 * Copyright (c) 2012-2014 Axelor. All Rights Reserved.
 */
package com.axelor.contact.web

import groovy.util.logging.Slf4j

import java.util.List

import com.axelor.contact.db.Address
import com.axelor.contact.db.Company
import com.axelor.contact.db.Contact
import com.axelor.contact.db.Country
import com.axelor.db.JPA
import com.axelor.rpc.ActionRequest
import com.axelor.rpc.ActionResponse

@Slf4j
class HelloController {
	
	void say(ActionRequest request, ActionResponse response) {

		def contact = request.context as Contact
		def message = "Welcome '${contact.fullName}!'"
		
		if (contact.addresses != null) {
			def total = contact.addresses.size()
			def selected = contact.addresses.findAll { it.selected }
			message += "<br>" + "You have selected ${selected.size()} record(s) from total ${total} address records."
		}
		
		log.info("send greetings to: ${contact.fullName}")
		
		response.flash = message
	}

	void about(ActionRequest request, ActionResponse response) {
		
		def address = request.context as Address
		def contact = request.context.parentContext as Contact
		
		def name = contact?.fullName
		
		if (name == null)
			name = contact?.firstName

		def message = "Where are you from?"
		if (address.country)
			message = "'${address.country.name}' is a beautiful country!"

		if (name)
			message = "Welcome '$name'...</br>" + message
			
		response.flash = message
	}
	
	void guessEmail(ActionRequest request, ActionResponse response) {

		def contact = request.context as Contact
		
		if (contact.email == null) {
			
			def email = [contact.firstName, contact.lastName].findAll { it != null }.join(".")  + "@gmail.com"
			def addresses = createAddresses()
			
			response.values = [email: email.toLowerCase(), addresses: addresses]
		}
	}
	
	private List<Address> createAddresses() {
		
		Country fr = Country.all().filter("code = ?", "FR").fetchOne()
		
		if (fr == null) {

			log.debug('creating a Country record: { code: "FR", name: "France"}')

			fr = new Country(code: "FR", name: "France")
			JPA.runInTransaction {
				fr.save()
			}
		}
		
		return [
			new Address(street: "My", area: "Home", city: "Paris", country: fr),
			new Address(street: "My", area: "Office", city: "Paris", country: fr)
		]
	}

	void showHomePage(ActionRequest request, ActionResponse response) {

		response.view = [title: "Axelor.com", resource: "http://www.axelor.com/", viewType: "html"]
	}

	void showCompanyList(ActionRequest request, ActionResponse response) {

		def contact = request.context as Contact
		def view = [title : "All Companies", resource : Company.class.name]
		
		if (contact.company != null) {
			view += [domain: "self.code = '${contact.company.code}' OR self.parent.code = '${contact.company.code}'"]
		}
		
		response.view = view
	}
}

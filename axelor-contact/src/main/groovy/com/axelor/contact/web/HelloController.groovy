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

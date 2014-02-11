package com.axelor.sale.service;

import com.axelor.contact.db.Contact;
import com.axelor.contact.service.HelloServiceImpl;

public class HelloServiceSaleImpl extends HelloServiceImpl {

	@Override
	public String say(Contact contact) {
		log.info("Overrding the default HelloService.say ...");
		String message = super.say(contact);
		log.info("The default message was: {}", message);
		message = String.format("You are welcome '%s!'", contact.getFullName());
		log.info("I would say: {}", message);
		return message;
	}
}

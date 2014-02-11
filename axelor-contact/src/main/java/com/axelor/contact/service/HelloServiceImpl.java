package com.axelor.contact.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.contact.db.Contact;

public class HelloServiceImpl implements HelloService {
	
	protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public String say(Contact contact) {
		return String.format("Welcome '%s!'", contact.getFullName());
	}
	
	@Override
	public String hello() {
		return "Hello world!!!";
	}
}

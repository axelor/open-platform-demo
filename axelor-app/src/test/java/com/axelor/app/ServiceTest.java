package com.axelor.app;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.axelor.contact.db.Contact;
import com.axelor.contact.service.HelloService;
import com.axelor.test.GuiceModules;
import com.axelor.test.GuiceRunner;

@RunWith(GuiceRunner.class)
@GuiceModules({ MyModule.class })
public class ServiceTest {

	@Inject
	private HelloService service;
	
	@Test
	public void test() {
		
		Contact contact = new Contact();
		contact.setFirstName("John");
		contact.setLastName("Smith");
	
		String said = service.say(contact);
		String hello = service.hello();
		
		Assert.assertEquals("You are welcome 'John Smith!'", said);
		Assert.assertEquals("Hello world!!!", hello);
	}
}

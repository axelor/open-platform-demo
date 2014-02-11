package com.axelor.contact;

import com.axelor.app.AxelorModule;
import com.axelor.app.AxelorModuleInfo;
import com.axelor.contact.service.HelloService;
import com.axelor.contact.service.HelloServiceImpl;

@AxelorModuleInfo(name = "axelor-contact")
public class ContactModule extends AxelorModule {

	@Override
	protected void configure() {
		bind(HelloService.class).to(HelloServiceImpl.class);
	}
}

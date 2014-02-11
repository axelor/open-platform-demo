package com.axelor.app;

import com.axelor.auth.AuthModule;
import com.axelor.db.JpaModule;
import com.google.inject.AbstractModule;

public class MyModule extends AbstractModule {
	
	@Override
	protected void configure() {
		install(new JpaModule("testUnit"));
		install(new AuthModule.Simple());
		install(new AppModule());
	}
}
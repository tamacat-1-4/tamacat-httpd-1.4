/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public final class SessionManager {
	static final Log LOG = LogFactory.getLog(SessionManager.class);
	
	private static SessionFactory FACTORY;
	private static final Thread CLEANER;

	static {
		DIContainer di = DI.configure("session.xml");
		FACTORY = di.getBean("session", SessionFactory.class);
		if (FACTORY == null) FACTORY = new DefaultSessionFactory();
		//start session cleaning thread.
		SessionCleaner cleaner = di.getBean("cleaner", SessionCleaner.class);
		cleaner.setSessionFactory(FACTORY);
		CLEANER = new Thread(cleaner, cleaner.getName());
		CLEANER.start();
	}

	public
	  static SessionFactory getInstance() {
		return FACTORY;
	}
	
	private SessionManager() {}
}

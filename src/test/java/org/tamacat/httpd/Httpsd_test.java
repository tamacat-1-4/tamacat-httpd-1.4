/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;

/**
 * <p>It is the start class of the http server.
 * The component setting in {@code components.xml}.
 */
public class Httpsd_test {

	public static final String XML = "httpsd.xml";
	
	public static void main(String[] args) {
		String config = args.length > 0 ? args[0] : XML;
		DIContainer di = DI.configure(config);
		if (di == null) throw new IllegalArgumentException(config + " is not found.");
		HttpEngine server = di.getBean("server", HttpEngine.class);
		if (server == null) throw new IllegalArgumentException();
		Thread t = new Thread(server);
		t.start();
	}
}

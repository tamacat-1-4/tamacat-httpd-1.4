/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.core.HttpEngine;
import org.tamacat.util.StringUtils;

/**
 * <p>It is the start class of the http server.
 */
public class Httpd {

	public static final String XML = "httpd.xml";
	private static final String DEFAULT_SERVER_KEY = "server";
	
	/**
	 * <p>Starting Http servers.
	 * @param args
	 *   <li>args[0]: Setting file of XML(s). ex) httpd.xml,httpsd.xml (default "httpd.xml")
	 *   <li>args[1]: Name of HttpEngine ID in xml. (default "server")
	 */
	public static void main(String[] args) {
		String config = args.length > 0 ? args[0] : XML;
		String serverKey = args.length > 1 ? args[1] : DEFAULT_SERVER_KEY;
		for (String xml : StringUtils.split(config, ",")) {
			DIContainer di = DI.configure(xml);
			if (di == null) throw new IllegalArgumentException(xml + " is not found.");
			HttpEngine server = di.getBean(serverKey, HttpEngine.class);
			if (server == null) throw new IllegalArgumentException();
			Thread t = new Thread(server);
			t.start();
		}
	}
}

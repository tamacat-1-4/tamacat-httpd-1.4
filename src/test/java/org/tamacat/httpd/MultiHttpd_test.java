/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

/**
 * <p>It is the start class of the http server.
 */
public class MultiHttpd_test {
	
	public static void main(String[] args) {
		Httpd.main(new String[]{"httpd.xml, httpsd.xml", "server"});
	}
}

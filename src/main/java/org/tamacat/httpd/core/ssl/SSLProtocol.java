/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

public enum SSLProtocol {
	SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1_1("TLSv1.1"), TLSv1_2("TLSv1.2"), SSL_TLS, SSL_TLSv2;

	private String name;

	private SSLProtocol() {}

	private SSLProtocol(String name) {
		this.name = name;
	}

	public String getName() {
		if (name != null) return name ;
		else return super.name();
	}
}

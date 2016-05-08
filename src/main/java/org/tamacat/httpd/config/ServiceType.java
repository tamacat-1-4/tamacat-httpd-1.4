/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

/**
 * <p>The type of service URL.
 */
public enum ServiceType implements UrlType {
	
	/**
	 * <p>The service url type of normal http server.
	 */
	NORMAL,
	
	/**
	 * <p>The service url type of reverse proxy.
	 */
	REVERSE,
	
	/**
	 * <p>The service url type of reverse proxy with load balancing.
	 */
	LB,
	
	/**
	 * <p>The service url type of error page.
	 */
	ERROR;
	
	/**
	 * <p>Find the String of {@code ServiceType} enum const object. 
	 * @param name
	 * @return When no enum const class, throws the {@code java.lang.IllegalArgumentException}.
	 */
	public static ServiceType find(String name) {
		return valueOf(name.toUpperCase());
	}
	
	@Override
	public String getType() {
		return toString();
	}
}

/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

/**
 * <p>It is the interface of the user object
 *  to use by the authentication.
 */
public interface AuthUser {
	
	/**
	 * Get the username (login id)
	 * @return username
	 */
	String getAuthUsername();
	
	/**
	 * Get the password
	 * @return password
	 */
	String getAuthPassword();
	
	/**
	 * Set the username(login id) to use by the authentication.
	 * @param password
	 */
	void setAuthUsername(String username);
	
	/**
	 * Set the password to use by the authentication.
	 * @param password
	 */
	void setAuthPassword(String password);
	
	/**
	 * When I use the encrypted password,
	 * I give back the {@code true}.
	 */
	boolean isEncrypted();
}

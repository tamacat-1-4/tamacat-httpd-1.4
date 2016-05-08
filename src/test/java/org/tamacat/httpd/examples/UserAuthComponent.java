/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.examples;

import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AbstractAuthComponent;

public class UserAuthComponent extends AbstractAuthComponent<User> {

	private String username;
	private String password;
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public User getAuthUser(String id, HttpContext context) {
		User user = new User();
		user.setAuthUsername(username);
		user.setAuthPassword(password);
		return user;
	}
}

/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.examples;

import org.tamacat.httpd.auth.CacheSupportAuthUser;

public class User implements CacheSupportAuthUser {

	private String username;
	private String password;
	private long createTime = System.currentTimeMillis();

	@Override
	public String getAuthPassword() {
		return password;
	}

	@Override
	public String getAuthUsername() {
		return username;
	}

	@Override
	public boolean isEncrypted() {
		return false;
	}

	@Override
	public void setAuthPassword(String password) {
		this.password = password;
	}

	@Override
	public void setAuthUsername(String username) {
		this.username = username;
	}
	
	@Override
	public boolean isCacheExpired(long expire) {
		return System.currentTimeMillis() - createTime > expire;
	}
}

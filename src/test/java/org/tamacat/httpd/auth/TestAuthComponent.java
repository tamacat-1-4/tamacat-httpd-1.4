package org.tamacat.httpd.auth;

import org.apache.http.protocol.HttpContext;

public class TestAuthComponent extends AbstractAuthComponent<AuthUserImpl> {

	private String authUsername;
	private String authPassword;

	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}
	
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	@Override
	public AuthUserImpl getAuthUser(String id, HttpContext context) {
		AuthUserImpl user = new AuthUserImpl();
		user.setAuthUsername(authUsername);
		user.setAuthPassword(authPassword);
		return user;
	}
}

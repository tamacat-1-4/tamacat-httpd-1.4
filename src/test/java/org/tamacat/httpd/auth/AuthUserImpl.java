package org.tamacat.httpd.auth;

public class AuthUserImpl implements AuthUser {

	private String authUsername;
	private String authPassword;
	private boolean isEncrypted;
	
	@Override
	public String getAuthUsername() {
		return authUsername;
	}
	@Override
	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}
	
	@Override
	public String getAuthPassword() {
		return authPassword;
	}
	
	@Override
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}
	
	@Override
	public boolean isEncrypted() {
		return isEncrypted;
	}
}

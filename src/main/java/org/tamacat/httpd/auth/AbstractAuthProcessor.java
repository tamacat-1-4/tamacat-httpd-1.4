/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.security.MessageDigest;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.filter.acl.FreeAccessControl;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * The abstract class of authentication processor.
 */
public abstract class AbstractAuthProcessor implements RequestFilter, ResponseFilter {

	static final Log LOG = LogFactory.getLog(AbstractAuthProcessor.class);
	
	protected AuthComponent<?> authComponent;
	protected String remoteUserKey = AuthComponent.REMOTE_USER_KEY;
	protected ServiceUrl serviceUrl;
	protected SingleSignOn singleSignOn;
	protected String algorithmName; // ex. SHA-256
	
	protected FreeAccessControl freeAccessControl = new FreeAccessControl();
	
	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
		if (authComponent != null) {
			authComponent.init();
		}
		this.freeAccessControl.setPath(serviceUrl.getPath());
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		if (authComponent != null) {
			authComponent.release();
		}
	}

	/**
	 * Set the {@link AuthComponent}. (required)
	 * 
	 * @param authComponent
	 */
	public void setAuthComponent(AuthComponent<?> authComponent) {
		this.authComponent = authComponent;
	}

	/**
	 * Set the remote user key name. (optional)
	 * 
	 * @param remoteUserKey
	 */
	public void setRemoteUserKey(String remoteUserKey) {
		this.remoteUserKey = remoteUserKey;
	}

	/**
	 * Get the Key name of Remote user.
	 * 
	 * @return remoteUserKey
	 */
	public String getRemoteUserKey() {
		return remoteUserKey;
	}

	/**
	 * Set the SingleSignOn object.
	 * 
	 * @param singleSignOn
	 */
	public void setSingleSignOn(SingleSignOn singleSignOn) {
		this.singleSignOn = singleSignOn;
	}
	
	/**
	 * The extension skipping by the certification in comma seperated values.
	 * @param extensions (CSV)
	 */
	public void setFreeAccessExtensions(String extensions) {
		this.freeAccessControl.setFreeAccessExtensions(extensions);
	}

	protected boolean isFreeAccess(HttpRequest request) {
		return isFreeAccess(RequestUtils.getPath(request));
	}
	
	protected boolean isFreeAccess(String path) {
		return freeAccessControl.isFreeAccess(path);
	}
	
	public void setFreeAccessUrl(String freeAccessUrl) {
		this.freeAccessControl.setFreeAccessUrl(freeAccessUrl);
	}
	
	/**
	 * Set the encryption algorithm for "getEncriptedPassword" method. ex. "SHA-256"
	 * @param algorithmName
	 */
	public void setPasswordEncryptionAlgorithm(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * Get the encrypted password. Please set up the encryption algorithm by a
	 * "setPasswordEncryptedAlgorithm" method in advance. if "algorithmName" is
	 * empty then returns a plain password.
	 * 
	 * @param password (Plain password)
	 * @return encrypted password or plain password(algorithm is empty)
	 */
	protected String getEncryptedPassword(String password) {
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(algorithmName)) {
			return password;
		}
		try {
			MessageDigest md = MessageDigest.getInstance(algorithmName);
			md.update(password.getBytes());
			byte[] digest = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				String hex = String.format("%02x", b);
				sb.append(hex);
			}
			return sb.toString();
		} catch (Exception e) {
			throw new UnauthorizedException();
		}
	}
}

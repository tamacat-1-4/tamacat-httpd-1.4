/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.util.StringUtils;
import org.tamacat.util.UniqueCodeGenerator;

/**
 * Implements of Digest authentication.
 */
public class DigestAuthProcessor extends AbstractAuthProcessor {

	static final String AUTHORIZATION = "Authorization";
	static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	protected String realm = "Authentication required";

	protected String algorithm = "MD5";
	protected String qop = "auth";

	/**
	 * Realm is changed. Default realm is "Authentication required".
	 *
	 * @param realm
	 */
	public void setRealm(String realm) {
		this.realm = DynamicRealm.getRealm(realm, new Date());
	}

	/**
	 * Set the algorithm. Default algorithm is MD5
	 *
	 * @param algorithm
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public void setPasswordEncryptionAlgorithm(String algorithmName) {
		this.algorithmName = null;
	}

	/**
	 * Set the qop. Dejault is "auth".
	 *
	 * @param qop
	 */
	public void setQop(String qop) {
		this.qop = qop;
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		if ("OPTIONS".equalsIgnoreCase(request.getRequestLine().getMethod())) {
			response.setStatusCode(HttpStatus.SC_NO_CONTENT);
			return;
		}
		if (isFreeAccess(request) == false) {
			try {
				String remoteUser = checkUser(request, response, context);
				context.setAttribute(remoteUserKey, remoteUser);
			} catch (UnauthorizedException e) {
				response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
				setWWWAuthenticateHeader(response);
				throw e;
			}
		}
	}

	/**
	 * When the user authentication check and correct, the username(login id) is
	 * returned.
	 *
	 * @param request
	 * @param response
	 * @param context
	 * @return username (login id)
	 * @throws UnauthorizedException
	 */
	protected String checkUser(HttpRequest request, HttpResponse response,
			HttpContext context) throws UnauthorizedException {
		Header digestAuthLine = request.getFirstHeader(AUTHORIZATION);
		if (digestAuthLine != null && StringUtils.isNotEmpty(digestAuthLine.getValue())) {
			String line = digestAuthLine.getValue().replaceFirst("Digest ", "");
			Digest digest = new Digest(line);
			if (authComponent != null) {
				AuthUser user = authComponent.getAuthUser(digest.getUsername(), context);
				if (user == null) {
					throw new UnauthorizedException();
				}
				String username = digest.getUsername();
				String password = digest.getResponse();
				String hashedPassword = DigestUtils.getHashedPassword(request, user, digest);
				if (username != null && password != null
						&& username.equals(user.getAuthUsername())
						&& password.equals(hashedPassword)) {
					if (singleSignOn != null) {
						singleSignOn.sign(username, request, response, context);
					}
					return user.getAuthUsername();
				}
			}
		}
		if (singleSignOn != null && singleSignOn.isSigned(request, response, context)) {
			return singleSignOn.getSignedUser(request, response, context);
		}
		throw new UnauthorizedException();
	}

	/**
	 * Set the "WWW-Authenticate" response header of Digest authenticate realm.
	 *
	 * @param response
	 */
	protected void setWWWAuthenticateHeader(HttpResponse response) {
		response.addHeader(WWW_AUTHENTICATE, "Digest realm=\"" + realm + "\", "
				+ "nonce=\"" + generateNonce() + "\", " + "algorithm="
				+ algorithm + ", qop=\"" + qop + "\"");
	}

	protected String generateNonce() {
		return UniqueCodeGenerator.generate();
	}
}

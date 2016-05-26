/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.session.Session;
import org.tamacat.httpd.session.SessionManager;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.httpd.util.HtmlUtils;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.DiagnosticContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * Implements of HTML Form based authentication.
 */
public class FormAuthProcessor extends AbstractAuthProcessor {

	static Log LOG = LogFactory.getLog(FormAuthProcessor.class);
	static final DiagnosticContext DC = LogFactory.getDiagnosticContext(LOG);

	protected static final String SC_AUTHORIZED = FormAuthProcessor.class.getName() + ".SC_AUTHORIZED";

	protected String charset = "UTF-8";
	protected String loginPageUrl = "login.html";
	protected String loginActionUrl = "check.html";
	protected String logoutActionUrl = "logout.html";
	protected String topPageUrl = "index.html";
	protected String usernameKey = "username";
	protected String passwordKey = "password";
	protected String redirectKey = "redirect";

	protected String sessionCookieName = "Session";
	protected String sessionUsernameKey = "SingleSignOnUser";
	
	/**
	 * Setting the HTTP charset parameter.
	 *
	 * @param charset default: "UTF-8"
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * Setting the form login page.
	 *
	 * @param loginPageUrl default: "login.html"
	 */
	public void setLoginPageUrl(String loginPageUrl) {
		this.loginPageUrl = loginPageUrl;
	}

	/**
	 * Setting the form action URL.
	 *
	 * <pre>
	 * &lt;form action="check.html"&gt;
	 * </pre>
	 *
	 * @param loginActionUrl
	 *            default: "check.html"
	 */
	public void setLoginActionUrl(String loginActionUrl) {
		this.loginActionUrl = loginActionUrl;
	}

	/**
	 * Setting the logout action URL.
	 *
	 * @param logoutActionUrl default: "logout.html"
	 */
	public void setLogoutActionUrl(String logoutActionUrl) {
		this.logoutActionUrl = logoutActionUrl;
	}

	/**
	 * URL on the top page that moves after log in is set.
	 *
	 * @param topPageUrl
	 */
	public void setTopPageUrl(String topPageUrl) {
		this.topPageUrl = topPageUrl;
	}

	/**
	 * Set the key of username for input text form.
	 *
	 * @param usernameKey
	 */
	public void setUsernameKey(String usernameKey) {
		this.usernameKey = usernameKey;
	}

	/**
	 * Set the key of password for input text form.
	 *
	 * @param passwordKey
	 */
	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}

	/**
	 * <p>
	 * Setting the request parameter of redirect key.
	 *
	 * @param redirectKey
	 *            default: "redirect"
	 */
	public void setRedirectKey(String redirectKey) {
		this.redirectKey = redirectKey;
	}

	/**
	 * Set the cookie name for session.
	 *
	 * @param sessionCookieName
	 */
	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	/**
	 * Set the key of username for session.
	 *
	 * @param sessionUsernameKey
	 */
	public void setSessionUsernameKey(String sessionUsernameKey) {
		this.sessionUsernameKey = sessionUsernameKey;
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response, HttpContext context) {
		// Get the session ID in client Cookie.
		String sessionId = HeaderUtils.getCookieValue(request, sessionCookieName);
		try {
			String remoteUser = null;
			String path = RequestUtils.getPath(request);
			if (path.endsWith(loginPageUrl)) {
				logoutAction(request, sessionId);
				return;
			} else if (isFreeAccess(path)) {
				if (sessionId != null) {
					Session session = SessionManager.getInstance().getSession(sessionId, false);
					if (session != null) {
						remoteUser = (String) session.getAttribute(sessionUsernameKey);
						if (remoteUser != null) {
							context.setAttribute(remoteUserKey, remoteUser);
							DC.setMappedContext("user", remoteUser);
						}
					}
				}
				return; // skip by this filter.
			} else if (isMatchLoginUrl(request)) {
				// login check
				remoteUser = checkUser(request, response, context);
				context.setAttribute(remoteUserKey, remoteUser);
				DC.setMappedContext("user", remoteUser);
				Session session = SessionManager.getInstance().createSession();
				session.setAttribute(sessionUsernameKey, remoteUser);
				response.setHeader("Set-Cookie", sessionCookieName + "=" + session.getId() + "; Path=/");
				context.setAttribute(SC_AUTHORIZED, Boolean.TRUE);
			} else if (StringUtils.isNotEmpty(sessionId)) {
				// already login. -> session check
				Session session = SessionManager.getInstance().getSession(sessionId, false);
				if (session == null) { // invalid session.
					throw new UnauthorizedException();
				}
				remoteUser = (String) session.getAttribute(sessionUsernameKey);
				if (remoteUser == null) { // invalid session.
					throw new UnauthorizedException();
				}
				context.setAttribute(remoteUserKey, remoteUser);
				DC.setMappedContext("user", remoteUser);
				if (path.endsWith(logoutActionUrl)) {
					// logout -> session delete -> login page.
					logoutAction(request, sessionId);
					// force login page.
					// context.setAttribute(SC_UNAUTHORIZED, Boolean.TRUE);
				} else {
					// OK
				}
			} else { // It does not yet login.
				throw new UnauthorizedException();
			}
		} catch (UnauthorizedException e) {
			logoutAction(request, sessionId);
			response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
			context.setAttribute(EXCEPTION_KEY, e);
		}
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response, HttpContext context) {
		try {
			if (isException(response, context)) {
				// unauthorized -> Go to the login page.
				sendRedirect(request, response, getLoginPageUrlWithRedirect(request));
			} else if (Boolean.TRUE.equals(context.getAttribute(SC_AUTHORIZED))) {
				// authorized login -> Go to the top page.
				String uri = RequestUtils.getParameter(context, "redirect");
				if (uri != null) {
					try {
						uri = URLDecoder.decode(uri, charset);
					} catch (Exception e) {
						// Invaid redirect URI -> goto top page.
						uri = topPageUrl;
					}
				} else {
					uri = topPageUrl;
				}
				sendRedirect(request, response, uri);
			}
		} finally {
			if (authComponent != null) {
				authComponent.release();
			}
		}
	}
	
	protected boolean isException(HttpResponse resp, HttpContext context) {
		Exception ex = (Exception) context.getAttribute(EXCEPTION_KEY);
		return ex != null || 401 == resp.getStatusLine().getStatusCode();
	}
	
	/**
	 * Redirect for login action.
	 *
	 * @param request
	 * @param response
	 * @param uri
	 *            redirect URI path.
	 */
	protected void sendRedirect(HttpRequest request, HttpResponse response, String uri) {
		try {
			response.setHeader(HTTP.CONTENT_TYPE, "text/html; charset=" + charset);
			response.setEntity(new StringEntity(
				"<html><meta http-equiv=\"refresh\" content=\"0;url="
				+ HtmlUtils.escapeHtmlMetaChars(uri) + "\"></html>", "UTF-8"));
		} catch (Exception e) {
			throw new UnauthorizedException();
		}
	}

	/**
	 * Logout the system with invalidate this session.
	 *
	 * @param sessionId
	 */
	protected void logoutAction(HttpRequest request, String sessionId) {
		if (StringUtils.isNotEmpty(sessionId)) {
			Session session = SessionManager.getInstance().getSession(sessionId, false);
			if (session != null) {
				session.invalidate();
			}
		}
	}

	/**
	 * Request URI confirms whether to match to loginActionUrl.
	 *
	 * @param request
	 * @return true: Request URI is considered to be loginActionURL and the
	 *         same.
	 */
	protected boolean isMatchLoginUrl(HttpRequest request) {
		return request.getRequestLine().getUri().endsWith(loginActionUrl);
	}

	/**
	 * After log in is attested, URL redirected to requested URL is acquired.
	 *
	 * @param request
	 * @return loginPageUrl?redirectKey=/path/to/requestURL
	 */
	protected String getLoginPageUrlWithRedirect(HttpRequest request) {
		String uri = request.getRequestLine().getUri(); //RequestUtils.getRequestPath(request);
		if (!uri.endsWith(logoutActionUrl) && !uri.endsWith(loginActionUrl)) {
			try {
				return loginPageUrl + "?" + redirectKey + "=" + URLEncoder.encode(uri, charset);
			} catch (Exception e) {
			}
		}
		return loginPageUrl;
	}

	/**
	 * Login check with AuthComponent.
	 *
	 * @param request
	 * @param response
	 * @param context
	 * @return login username in request parameter.
	 * @throws UnauthorizedException
	 */
	protected String checkUser(HttpRequest request, HttpResponse response,
			HttpContext context) throws UnauthorizedException {
		String username = RequestUtils.getParameter(context, usernameKey);
		String password = RequestUtils.getParameter(context, passwordKey);
		if (StringUtils.isNotEmpty(username)) {
			if (authComponent != null && authComponent.getAuthUser(username, context) != null) {
				if (authComponent.getAuthUser(username, context).isEncrypted()) {
					password = getEncryptedPassword(password);
				}
				if (authComponent.check(username, password, context)) {
					if (singleSignOn != null) {
						singleSignOn.sign(username, request, response, context);
					}
					return username;
				}
			}
		} else if (singleSignOn != null && singleSignOn.isSigned(request, response, context)) {
			return singleSignOn.getSignedUser(request, response, context);
		}
		throw new UnauthorizedException();
	}
}
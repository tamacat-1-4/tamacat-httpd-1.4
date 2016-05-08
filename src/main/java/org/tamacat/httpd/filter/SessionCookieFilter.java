/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.session.Session;
import org.tamacat.httpd.session.SessionFactory;
import org.tamacat.httpd.session.SessionManager;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.util.StringUtils;

public class SessionCookieFilter implements RequestFilter {

	protected ServiceUrl serviceUrl;
	private static final String SESSION_ATTRIBUTE_KEY = Session.class.getName();
	private String sessionCookieName = "Session";
	
	protected String getSessionCookieName() {
		return sessionCookieName;
	}

	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	protected static final SessionFactory MANAGER = SessionManager.getInstance();
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		Header[] headers = request.getHeaders("Cookie");
		synchronized(MANAGER) {
			Session session = null;
			if (headers != null) {
				for (Header header : headers) {
					String id = HeaderUtils.getCookieValue(
						header.getValue(), getSessionCookieName());
					if (StringUtils.isNotEmpty(id)) {
						session = MANAGER.getSession(id, false);
						break;
					}
				}
			}

			if (session == null) {
				session = MANAGER.createSession();
				response.addHeader("Set-Cookie", 
					getSessionCookieName() + "=" + session.getId() + "; Path=/");
				request.addHeader("Cookie", 
					getSessionCookieName() + "=" + session.getId()); //for ReverseProxy
			}
			context.setAttribute(SESSION_ATTRIBUTE_KEY, session);
		}
	}

	@Override
	public void init(ServiceUrl serviceUrl) {	
		this.serviceUrl = serviceUrl;
	}
}

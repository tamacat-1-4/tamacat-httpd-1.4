/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AbstractAuthProcessor;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

@Deprecated
/**
 * @see org.tamacat.httpd.auth.CookieBasedSingleSignOn
 */
public class SingleSignOnFilter extends AbstractAuthProcessor {

	static final Log LOG = LogFactory.getLog(SingleSignOnFilter.class);

	protected String singleSignOnCookieName;
	
	public SingleSignOnFilter(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}
	
	public SingleSignOnFilter() {
		this.singleSignOnCookieName = "SingleSignOnUser";
	}
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		String remoteUser = (String) context.getAttribute(remoteUserKey);
		if (StringUtils.isNotEmpty(remoteUser)) {
			Header[] cookieHeaders = request.getHeaders("Cookie");
			String user = null;
			for (Header h : cookieHeaders) {
				String cookie = h.getValue();
				user = HeaderUtils.getCookieValue(cookie, singleSignOnCookieName);
				LOG.trace("CookieUser: " + user);
				if (StringUtils.isNotEmpty(user)) {
					break;
				}
			}
			if (StringUtils.isEmpty(user)) {
				response.setHeader("Set-Cookie", singleSignOnCookieName + "=" + remoteUser + "; Path=/");
				request.setHeader("Cookie",	singleSignOnCookieName + "=" + remoteUser); //for Reverse Proxy
				LOG.trace("Set-Cookie: " + singleSignOnCookieName + "=" + remoteUser + "; Path=/");
			}
		} else {
			if (isFreeAccess(request) == false) {
				throw new UnauthorizedException();
			}
		}
	}

	public void setSingleSignOnCookieName(String singleSignOnCookieName) {
		this.singleSignOnCookieName = singleSignOnCookieName;
	}
}

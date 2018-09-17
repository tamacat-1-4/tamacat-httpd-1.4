/*
 * Copyright (c) 2018 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * Response Filter for Set-Cookie response header adding Secure/HttpOnly attributes.
 * 
 * <li>httpSecureEnabled: default false: always add attributes.</li>
 * <li>checkParentRequestHeader: default "X-Forwarded-For"
 *    if httpSecureEnabled=true and request header exists then add attributes.</li>
 *    
 * <li>isHttpOnly: true: add HttpOnly attribute. (default false)</li>
 * <li>Secure: true: add Secure attribute. (default false)</li>
 * @since 1.4
 */
public class SetCookieConvertFilter implements RequestFilter, ResponseFilter {

	static final Log LOG = LogFactory.getLog(SetCookieConvertFilter.class);
	
	static final String CONTEXT_SET_COOKIE_CONVERT = "SetCookieConvertFilter.__SET_COOKIE_CONVERT__";
	
	protected ServiceUrl serviceUrl;

	protected boolean httpSecureEnabled; //default false: always convert.
	protected String checkParentRequestHeader = "X-Forwarded-For";
	
	protected boolean isHttpOnly;
	protected boolean isSecure;

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	@Override
	public void doFilter(HttpRequest req, HttpResponse resp, HttpContext context) {
		//for Internal http access.
		if (checkAddSecureEnabled(req) == false) {
			context.setAttribute(CONTEXT_SET_COOKIE_CONVERT, "skip");
		}
	}
	
	@Override
	public void afterResponse(HttpRequest req, HttpResponse resp, HttpContext context) {
		//for Internal http access.
		if ("skip".equals(context.getAttribute(CONTEXT_SET_COOKIE_CONVERT))) {
			return;
		}
		if (isHttpOnly || isSecure) {
			Header[] headers = resp.getHeaders("Set-Cookie");
			for (Header header : headers) {
				String value = header.getValue();
				if (StringUtils.isNotEmpty(value)) {
					String convertedValue = convertSetCookieValue(value);
					LOG.trace("[Filter] "+value +" => "+convertedValue);
					resp.removeHeader(header);
					resp.addHeader(new BasicHeader("Set-Cookie", convertedValue));
				}
			}
		}
	}

	protected boolean checkAddSecureEnabled(HttpRequest req) {
		//Always add Set-Cookie Secure attribute.
		if (httpSecureEnabled == false) return true;
		
		String header = HeaderUtils.getHeader(req, checkParentRequestHeader);
		if (StringUtils.isNotEmpty(header)) {
			return true; //use Load baranser. Add Set-Cookie Secure attribute.
		} else {
			return false; //Do Not add Secure attribute.
		}
	}
	
	protected String convertSetCookieValue(String headerValue) {
		StringBuilder convertedValue = new StringBuilder(headerValue.replaceAll(";$",""));
		String[] values = StringUtils.split(headerValue, ";");
		boolean containsHttpOnly = false;
		boolean containsSecure = false;
		for (String value : values) {
			String v = value.trim().toLowerCase();
			if ("httponly".equals(v)) {
				containsHttpOnly = true;
				continue;
			}
			if ("secure".equals(v)) {
				containsSecure = true;
				continue;
			}
		}
		if (isHttpOnly && containsHttpOnly==false) {
			convertedValue.append("; HttpOnly");
		}
		if (isSecure && containsSecure==false) {
			convertedValue.append("; Secure");
		}
		return convertedValue.toString();
	}

	public void setHttpOnly(boolean isHttpOnly) {
		this.isHttpOnly = isHttpOnly;
	}

	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}
	
	public void setHttpSecureEnabled(boolean httpSecureEnabled) {
		this.httpSecureEnabled = httpSecureEnabled;
	}

	public void setCheckParentRequestHeader(String checkParentRequestHeader) {
		this.checkParentRequestHeader = checkParentRequestHeader;
	}
}

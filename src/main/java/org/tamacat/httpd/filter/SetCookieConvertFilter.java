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
 * <li>useForwardedProto: default false. true: for TEST http access, DO NOT Add Secure attribute. (delete Secure attribute)</li>
 * <li>httpSecureEnabled: default false: http and https always add Secure attributes.</li>
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
	protected boolean useForwardedProto; //X-Forwarded-Proto: http -> DO NOT ADD Secure attribute.

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
		if (checkFilterEnabled(req) == false) {
			context.setAttribute(CONTEXT_SET_COOKIE_CONVERT, "skip");
		}
	}
	
	@Override
	public void afterResponse(HttpRequest req, HttpResponse resp, HttpContext context) {
		//for Internal http access.
		if ("skip".equals(context.getAttribute(CONTEXT_SET_COOKIE_CONVERT))) {
			return;
		}
		if (isSecure || isHttpOnly) {
			Header[] headers = resp.getHeaders("Set-Cookie");
			for (Header header : headers) {
				String value = header.getValue();
				if (StringUtils.isNotEmpty(value)) {
					String convertedValue = convertSetCookieValue(req, value);
					LOG.trace("[Filter] "+value +" => "+convertedValue);
					resp.removeHeader(header);
					resp.addHeader(new BasicHeader("Set-Cookie", convertedValue));
				}
			}
		}
	}

	protected boolean checkForwardedProtoHttp(HttpRequest req) {
		String proto = HeaderUtils.getHeader(req, "X-Forwarded-Proto");
		if ("http".equalsIgnoreCase(proto)) {
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean checkFilterEnabled(HttpRequest req) {
		//http and https Always add Set-Cookie Secure attribute.
		if (useForwardedProto || httpSecureEnabled == false) return true;
		
		//for TEST http access DO NOT add Secure attribute.
		String header = HeaderUtils.getHeader(req, checkParentRequestHeader);
		if (StringUtils.isNotEmpty(header)) {
			return true; //use Load baranser. Add Set-Cookie Secure attribute.
		} else {
			return false; //Do Not add Secure attribute.
		}
	}
	
	protected String convertSetCookieValue(HttpRequest req, String headerValue) {
		StringBuilder convertedValue = new StringBuilder();
		String[] values = StringUtils.split(headerValue.replaceAll(";$",""), ";");
		//delete HttpOnly and Secure attributes.
		for (String value : values) {
			String v = value.trim().toLowerCase();
			//delete HttpOnly attribute.
			if ("httponly".equals(v) && isHttpOnly) {
				continue;
			}
			//delete Secure attribute.
			if ("secure".equals(v)) {
				continue;
			}
			if (convertedValue.length() == 0) {
				convertedValue.append(value);
			} else {
				convertedValue.append("; " + value);
			}
		}
		if(convertedValue.length()==0) {
			return headerValue;
		}
		if (isHttpOnly) {
			convertedValue.append("; HttpOnly");
		}
		//Add Secure attribute. for isSecure and https (NOT TEST http access)
		if (isSecure && checkForwardedProtoHttp(req)==false) {
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
	
	/**
	 * Set-Cookie Secure attribute ADD and DELETE using X-Forwarded-Proto request header.
	 * true: "X-Forwarded-Proto: http" then DO NOT ADD Secure attribute and DELETE Secure attribute.
	 * @param useForwardedProto
	 * @since 1.4-20190410
	 */
	public void setUseForwardedProto(boolean useForwardedProto) {
		this.useForwardedProto = useForwardedProto;
	}
	
	public void setCheckParentRequestHeader(String checkParentRequestHeader) {
		this.checkParentRequestHeader = checkParentRequestHeader;
	}
}

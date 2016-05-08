/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.log.DiagnosticContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * <p>Access log utility.<br>
 *
 * Log category : Access
 *
 * <p>logging for:
 * <ul>
 *  <li>Remote IP address</li>
 *  <li>Access time</li>
 *  <li>Remote Username</li>
 *  <li>URL</li>
 *  <li>HTTP status code</li>
 *  <li>Content-Length(size)</li>
 *  <li>Response time</li>
 * </ul>
 */
public class AccessLogUtils {

	static final Log ACCESS_LOG = LogFactory.getLog("Access");
	static final DiagnosticContext DC = LogFactory.getDiagnosticContext(ACCESS_LOG);

	/**
	 * Write the access log.
	 * @param context Before set the remote IP address and username.
	 * @param time Response time
	 */
	static
	public void writeAccessLog(
			HttpRequest request, HttpResponse response,
			HttpContext context, long time) {
		writeAccessLog(request, response, context, time, null);
	}
	
	/**
	 * Write the access log.
	 * @param context Before set the remote IP address and username.
	 * @param time Response time
	 */
	static
	public void writeAccessLog(
			HttpRequest request, HttpResponse response,
			HttpContext context, long time, String forwardHeader) {
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		String uri = request.getRequestLine().getUri();
		int statusCode = response.getStatusLine().getStatusCode();
		String reasonPhrase = response.getStatusLine().getReasonPhrase();
		String proto = request.getProtocolVersion().toString();
		String ip = RequestUtils.getRemoteIPAddress(request, context, forwardHeader != null, forwardHeader);
		if (ip == null) ip = "";
		String remoteUser = (String) context.getAttribute(AuthComponent.REMOTE_USER_KEY);
		if (StringUtils.isEmpty(remoteUser)) remoteUser = "-";
		HttpEntity entity = response.getEntity();
		long size = entity != null ? entity.getContentLength() : 0;
		if (size == -1) {
			String contentLen= HeaderUtils.getHeader(response, HTTP.CONTENT_LEN);
			if (StringUtils.isNotEmpty(contentLen)) {
				size = StringUtils.parse(contentLen, -1L);
			}
		}
		DC.setMappedContext("ip", ip);
		DC.setMappedContext("user", remoteUser);
		String message = method + " " + uri + " " + proto +" " + statusCode
		+ " [" + reasonPhrase + "] " + size + " (" + time + "ms)";
		if (statusCode < 500) {
			ACCESS_LOG.info(message);
		} else {
			ACCESS_LOG.error(message);
		}
	}
}

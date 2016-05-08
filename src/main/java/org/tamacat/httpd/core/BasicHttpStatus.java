/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.HashMap;

/**
 * <p>Constants of HTTP Status Code and Reason Phrase.<br>
 * RFC 2616 Hypertext Transfer Protocol -- HTTP/1.1 (June 1999)
 * @see {@link http://www.ietf.org/rfc/rfc2616.txt}
 */
public enum BasicHttpStatus implements HttpStatus {

	//RFC 2616(HTTP/1.1) Informational 1xx
	SC_CONTINUE(100, "Continue"),
	SC_SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	SC_PROCESSING(102, "Processing"),

	//RFC 2616(HTTP/1.1) 10.2 Success 2xx
	SC_OK(200, "OK"),
	SC_CREATED(201, "Created"),
	SC_ACCEPTED(202,"Accepted"),
	SC_NON_AUTHORITATIVE_INFORMATION(203,"Non-Authoritative Information"),
	SC_NO_CONTENT(204,"No Content"),
	SC_RESET_CONTENT(205,"Reset Content"),
	SC_PARTIAL_CONTENT(206,"Partial Content"),
	SC_MULTI_STATUS(207,"Multi-Status"),
	SC_IM_USED(226,"IM Used"),

	//RFC 2616(HTTP/1.1) 10.3 Redirection 3xx
	SC_MULTIPLE_CHOICES(300,"Multiple Choices"),
	SC_MOVED_PERMANENTLY(301,"Moved Permanently"),
	SC_FOUND(302,"Found"), // RFC2068 -> Moved Temporarily
	SC_SEE_OTHER(303,"See Other"),
	SC_NOT_MODIFIED(304,"Not Modified"),
	SC_USE_PROXY(305,"Use Proxy"),
	SC_UNUSED(306,"(Unused)"),
	SC_TEMPORARY_REDIRECT(307,"Temporary Redirect"),

	//RFC 2616(HTTP/1.1) 10.4 Client Error 4xx
	SC_BAD_REQUEST(400,"Bad Request"),
	SC_UNAUTHORIZED(401,"Unauthorized"),
	SC_PAYMENT_REQUIRED(402,"Payment Required"),
	SC_FORBIDDEN(403, "Forbidden"),
	SC_NOT_FOUND(404, "Not Found"),
	SC_METHOD_NOT_ALLOWED(405,"Method Not Allowed"),
	SC_NOT_ACCEPTABLE(406,"Not Acceptable"),
	SC_PROXY_AUTHENTICATION_REQUIRED(407,"Proxy Authentication Required"),
	SC_REQUEST_TIMEOUT(408,"Request Timeout"),
	SC_CONFLICT(409,"Conflict"),
	SC_GONE(410,"Gone"),
	SC_LENGTH_REQUIRED(411,"Length Required"),
	SC_PRECONDITION_FAILED(412,"Precondition Failed"),
	SC_REQUEST_ENTITY_TOO_LARGE(413,"Request Entity Too Large"),
	SC_REQUEST_URI_TOO_LONG(414,"Request-URI Too Long"),
	SC_UNSUPPORTED_MEDIA_TYPE(415,"Unsupported Media Type"),
	SC_REQUESTED_RANGE_NOT_SATISFIABLE(416,"Requested Range Not Satisfiable"),
	SC_EXPECTATIONFAILED(417,"Expectation Failed"),
	SC_IM_A_TEAPOT(418,"I'm a teapot"),
	SC_UNPROCESSABLE_ENTITY(422,"Unprocessable Entity"),
	SC_LOCKED(423,"Locked"),
	SC_FAILED_DEPENDENCY(424,"Failed Dependency"),
	SC_UPGRADE_REQUIRED(426,"Upgrade Required"),

	//RFC 2616(HTTP/1.1) 10.5 Server Error 4xx
	SC_INTERNAL_SERVER_ERROR(500,"Internal Server Error"),
	SC_NOT_IMPLEMENTED(501,"Not Implemented"),
	SC_BAD_GATEWAY(502,"Bad Gateway"),
	SC_SERVICE_UNAVAILABLE(503,"Service Unavailable"),
	SC_GATEWAY_TIMEOUT(504,"Gateway Timeout"),
	SC_HTTP_VERSION_NOT_SUPPORTED(505,"HTTP Version Not Supported"),
	SC_VARIANT_ALSO_NEGOTIATES(506,"Variant Also Negotiates"),
	SC_INSUFFICIENT_STORAGE(507,"Insufficient Storage"),
	SC_NOT_EXTENDED(510,"Not Extended"),

	//Undefined RFC 2615 status code.
	SC_UNKNOWN(-1,"Unknown");

	private final int statusCode;
	private final String reasonPhrase;

	BasicHttpStatus(int statusCode, String reasonPhrase) {
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	static HashMap<Integer,HttpStatus> find = new HashMap<>();

	static {
		for (HttpStatus s : values()) {
			find.put(Integer.valueOf(s.getStatusCode()), s);
		}
	}

	public static HttpStatus getHttpStatus(int statusCode) {
		HttpStatus status = find.get(Integer.valueOf(statusCode));
		if (status != null) return status;
		else return SC_UNKNOWN;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public boolean isInformational() {
		return 100 <= statusCode && statusCode <= 199;
	}

	public boolean isSuccess() {
		return 200 <= statusCode && statusCode <= 299;
	}

	public boolean isRedirection() {
		return 300 <= statusCode && statusCode <= 399;
	}

	public boolean isClientError() {
		return 400 <= statusCode && statusCode <= 499;
	}

	public boolean isServerError() {
		return 500 <= statusCode && statusCode <= 599;
	}
}

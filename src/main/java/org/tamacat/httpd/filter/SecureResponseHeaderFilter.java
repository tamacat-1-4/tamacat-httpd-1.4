/*
 * Copyright (c) 2019 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.util.MimeUtils;
import org.tamacat.util.StringUtils;

/**
 * Add Secure Response Header.
 * This Filter for security measures to be set when header is not set.
 * 
 * Adding response headers. (default)
 * <pre>
 * X-Frame-Options: DENY
 * X-ContentType-Options: nosniff
 * X-XSS-Protection: 1; mode=block
 * Expires: Thu, 01 Jan 1970 00:00:00 GMT
 * Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0 Pragma: no-cache
 * </pre>
 * 
 * When Content-Type header is not set, Content-type is determined from mime-types.properties
 * based on the extension and set in response header. (default: "text/html; charset=UTF-8")
 */
public class SecureResponseHeaderFilter implements ResponseFilter {
	
	protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
	protected String defaultContentType = DEFAULT_CONTENT_TYPE;
	
	protected String frameOptions = "DENY";
	protected String contentTypeOptions = "nosniff";
	protected String xssProtection = "1; mode=block";
	protected String expires = "Thu, 01 Jan 1970 00:00:00 GMT";
	protected String cacheControl = "no-store, no-cache, must-revalidate, post-check=0, pre-check=0";
	protected String pragma = "no-cache";

	@Override
	public void init(ServiceUrl serviceUrl) {
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response, HttpContext context) {
		if (StringUtils.isNotEmpty(frameOptions) && response.containsHeader("X-Frame-Options") == false) {
			response.setHeader("X-Frame-Options", frameOptions);
		}
		if (StringUtils.isNotEmpty(contentTypeOptions) && response.containsHeader("X-Content-Type-Options") == false) {
			response.setHeader("X-Content-Type-Options", contentTypeOptions);
		}
		if (StringUtils.isNotEmpty(xssProtection) && response.containsHeader("X-XSS-Protection") == false) {
			response.setHeader("X-XSS-Protection", xssProtection);
		}
		if (StringUtils.isNotEmpty(expires) && response.containsHeader(HttpHeaders.EXPIRES) == false) {
			response.setHeader(HttpHeaders.EXPIRES, expires);
		}
		if (StringUtils.isNotEmpty(cacheControl) && response.containsHeader(HttpHeaders.CACHE_CONTROL) == false) {
			response.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
		}
		if (StringUtils.isNotEmpty(pragma) && response.containsHeader(HttpHeaders.PRAGMA) == false) {
			response.setHeader(HttpHeaders.PRAGMA, pragma);
		}
		
		if (response.getStatusLine().getStatusCode() <= 200 && response.getStatusLine().getStatusCode() < 300
		  && response.getEntity() != null
		  && StringUtils.isEmpty(response.getEntity().getContentType())
		  && response.containsHeader(HttpHeaders.CONTENT_TYPE) == false) {
			response.setHeader(HttpHeaders.CONTENT_TYPE, getContentType(request.getRequestLine().getUri()));
		}
	}

	public void setFrameOptions(String frameOptions) {
		this.frameOptions = frameOptions;
	}

	public void setContentTypeOptions(String contentTypeOptions) {
		this.contentTypeOptions = contentTypeOptions;
	}

	public void setXssProtection(String xssProtection) {
		this.xssProtection = xssProtection;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	public void setPragma(String pragma) {
		this.pragma = pragma;
	}
	
	public void setDefaultContentType(String defaultContentType) {
		this.defaultContentType = defaultContentType;
	}
	
	protected String getContentType(String path) {
		try {
			String contentType = MimeUtils.getContentType(path);
			if (StringUtils.isNotEmpty(contentType)) {
				return contentType;
			}
		} catch (Exception e) {
		}
		return defaultContentType;
	}
}

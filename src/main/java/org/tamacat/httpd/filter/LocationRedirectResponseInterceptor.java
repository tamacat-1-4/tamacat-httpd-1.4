package org.tamacat.httpd.filter;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.util.StringUtils;

public class LocationRedirectResponseInterceptor implements HttpResponseInterceptor {
	
	protected static final String LAST_REDIRECT_URL ="ReverseProxyHandler.LAST_REDIRECT_URL";

	@Override
	public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
		if (response.containsHeader("Location")) {
			String location = HeaderUtils.getHeader(response, "Location");
			if (StringUtils.isNotEmpty(location)) {
				context.setAttribute(LAST_REDIRECT_URL, location);
			}
		}
	}
	
	public static void checkRedirect(HttpResponse response, HttpContext context) {
		Object location = context.getAttribute(LAST_REDIRECT_URL);
		if (location != null && location instanceof String) {
			response.setStatusCode(302);
			response.addHeader("Location", (String)location);
		}
	}
}

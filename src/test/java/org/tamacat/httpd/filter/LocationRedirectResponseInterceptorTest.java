package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import org.junit.Test;

public class LocationRedirectResponseInterceptorTest {

	@Test
	public void testProcess() throws HttpException, IOException {
		HttpResponse response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
		response.addHeader("Location", "http://www.example.com/ridirect");
		HttpContext context = new BasicHttpContext();
		LocationRedirectResponseInterceptor interceptor = new LocationRedirectResponseInterceptor();
		interceptor.process(response, context);
		assertEquals("http://www.example.com/ridirect", context.getAttribute(LocationRedirectResponseInterceptor.LAST_REDIRECT_URL));
	}

	@Test
	public void testCheckRedirect() {
		HttpResponse response = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
		HttpContext context = new BasicHttpContext();
		context.setAttribute(LocationRedirectResponseInterceptor.LAST_REDIRECT_URL, "http://www.example.com/ridirect");
		LocationRedirectResponseInterceptor.checkRedirect(response, context);
		assertEquals("http://www.example.com/ridirect", response.getFirstHeader("Location").getValue());
	}
}

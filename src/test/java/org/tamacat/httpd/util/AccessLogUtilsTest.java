package org.tamacat.httpd.util;

import java.net.InetAddress;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class AccessLogUtilsTest {
	
	HttpRequest request;
	HttpResponse response;
	private HttpContext context;

	@Before
	public void setUp() throws Exception {
		context = HttpObjectFactory.createHttpContext();
		request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		response = HttpObjectFactory.createHttpResponse(200, "OK");
		
		InetAddress address = InetAddress.getByName("127.0.0.1");
		context.setAttribute(RequestUtils.REMOTE_ADDRESS, address);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteAccessLog() {
		long time = 123L;
		AccessLogUtils.writeAccessLog(request, response, context, time);
	}
}

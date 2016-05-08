package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.auth.AuthComponent;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.mock.HttpObjectFactory;
import org.tamacat.httpd.util.RequestUtils;

public class AccessLogFilterTest {

	AccessLogFilter filter;
	HttpRequest request;
	HttpResponse response;
	HttpContext context;
	ServiceUrl serviceUrl;
	
	@Before
	public void setUp() throws Exception {
		filter = new AccessLogFilter();
		request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		response = HttpObjectFactory.createHttpResponse(200, "OK");
		context = HttpObjectFactory.createHttpContext();
		InetAddress address = InetAddress.getByName("127.0.0.1");
		context.setAttribute(RequestUtils.REMOTE_ADDRESS, address);
		context.setAttribute(AuthComponent.REMOTE_USER_KEY, "admin");
		ServerConfig config = new ServerConfig();
		serviceUrl = new ServiceUrl(config);
		filter.init(serviceUrl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoFilter() {
		try {
			filter.doFilter(request, response, context);
			filter.afterResponse(request, response, context);
		} catch (Exception e) {
			fail();
		}
	}
}

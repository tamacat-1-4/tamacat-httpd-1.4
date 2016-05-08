package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.mock.HttpObjectFactory;

@Deprecated
public class SingleSignOnFilterTest {

	private SingleSignOnFilter filter;
	
	@Before
	public void setUp() throws Exception {
		filter = new SingleSignOnFilter();
		ServerConfig config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		filter.init(serviceUrl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoFilter() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		context.setAttribute(filter.getRemoteUserKey(), "admin");
		
		filter.doFilter(request, response, context);
		assertEquals("SingleSignOnUser=admin; Path=/",
				response.getFirstHeader("Set-Cookie").getValue());
	}

	@Test
	public void testSetSingleSignOnCookieName() {
		filter.setSingleSignOnCookieName("RemoteUser");
		assertEquals("RemoteUser", filter.singleSignOnCookieName);
	}

	@Test
	public void testSetRemoteUserKey() {
		filter.setRemoteUserKey("RemoteUser");
		assertEquals("RemoteUser", filter.getRemoteUserKey());
	}
}

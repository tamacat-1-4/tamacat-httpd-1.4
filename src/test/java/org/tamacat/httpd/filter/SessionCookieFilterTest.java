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
import org.tamacat.httpd.session.Session;

public class SessionCookieFilterTest {

	private static final String SESSION_ATTRIBUTE_KEY = Session.class.getName();

	private SessionCookieFilter filter;
	
	@Before
	public void setUp() throws Exception {
		filter = new SessionCookieFilter();
		ServerConfig config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		filter.init(serviceUrl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSessionCookieName() {
		assertEquals("Session", filter.getSessionCookieName());
		filter.setSessionCookieName("jsessionid");
		assertEquals("jsessionid", filter.getSessionCookieName());
	}

	@Test
	public void testDoFilter() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(request, response, context);
		Session session = (Session) context.getAttribute(SESSION_ATTRIBUTE_KEY);
		assertNotNull(session);
		assertNotNull(session.getId());
		assertEquals("Session=" + session.getId() + "; Path=/",
				response.getFirstHeader("Set-Cookie").getValue());
	}
}

package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.auth.WSSEAuthProcessor.WSSE;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class WSSEAuthProcessorTest {

	ServerConfig config;
	WSSEAuthProcessor auth;
	HttpRequest request;
	HttpResponse response;
	HttpContext context;
	TestAuthComponent authComponent;
	
	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		auth = new WSSEAuthProcessor();
		auth.init(serviceUrl);
		
		authComponent = new TestAuthComponent();
		authComponent.setAuthUsername("admin");
		authComponent.setAuthPassword("pass");
		auth.setAuthComponent(authComponent);
		request = HttpObjectFactory.createHttpRequest("GET", "/");
		response = HttpObjectFactory.createHttpResponse(200, "OK");
		context = new BasicHttpContext();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDoFilter() {
		try {
			auth.doFilter(request, response, context);
			fail();
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCheckUser() {
		try {
			String id = auth.checkUser(request, response, context);
			assertNull(id);
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}
		String line = "Username=\"admin\", "
				+ "PasswordDigest=\"nP4Vzuz70bhQk0FI3jsXq3CubIw=\", "
				+ "Nonce=\"OWYxM2VjNjA0NjQzOTZjMg==\", "
				+ "Created=\"2011-01-01T00:00:00Z\"";
		try {
			WSSE wsse = new WSSE(line);
			assertEquals("admin", wsse.getUsername());
			assertEquals("nP4Vzuz70bhQk0FI3jsXq3CubIw=", wsse.getPasswordDigest());
			assertEquals("OWYxM2VjNjA0NjQzOTZjMg==", wsse.getNonce());
			
			AuthUser user = authComponent.getAuthUser(wsse.getUsername(), context);
			assertEquals("admin", user.getAuthUsername());
			assertEquals("pass", user.getAuthPassword());
			
			assertEquals("nP4Vzuz70bhQk0FI3jsXq3CubIw=", auth.getPasswordDigest(wsse, user));
			
			request.setHeader(WSSEAuthProcessor.X_WSSE_HEADER, "UsernameToken " + line);
			
			String id = auth.checkUser(request, response, context);
			assertNotNull(id);
			assertEquals("admin", id);
		} catch (UnauthorizedException e) {
			fail();
		}
	}

	@Test
	public void testGetPasswordDigest() {
		//auth.getPasswordDigest(wsse, user);
	}

	@Test
	public void testSetWWWAuthenticateHeader() {
		auth.setWWWAuthenticateHeader(response);
	}

	@Test
	public void testGetNonce() {
		auth.getNonce();
	}

	@Test
	public void testGetSHA1() {
		//auth.getSHA1(digest)
	}

}

package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class HtmlLinkConvertInterceptorTest {

	private HtmlLinkConvertInterceptor target;
	
	@Before
	public void setUp() throws Exception {
		target = new HtmlLinkConvertInterceptor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		try {
			target.process(response, context);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testSetContentType() {
		target.setContentType("text/html");
		target.setContentType(" text/x-html ");
		target.setContentType("html,plain,css,javascript");
		target.setContentType("");
		target.setContentType(null);
	}
}

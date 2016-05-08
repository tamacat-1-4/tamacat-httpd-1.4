package org.tamacat.httpd.handler;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class HostRequestHandlerMapperTest {

	HostRequestHandlerMapper mapper;

	@Before
	public void setUp() throws Exception {
		mapper = new HostRequestHandlerMapper();

		ServerConfig config = new ServerConfig();
		String componentsXML = "components.xml";
		mapper.create(config, componentsXML);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLookup() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpContext context = HttpObjectFactory.createHttpContext();
		assertEquals(VelocityHttpHandler.class, mapper.lookup(request, context).getClass());
	}

	@Test
	public void testLookupException() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "");
		HttpContext context = HttpObjectFactory.createHttpContext();
		assertNull(mapper.lookup(request, context));
	}
}

package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;

public class RequestHeaderConvertFilterTest {

	RequestHeaderConvertFilter filter;
	
	@Before
	public void setUp() throws Exception {
		ServerConfig config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		filter = new RequestHeaderConvertFilter();
		filter.init(serviceUrl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertHeaderValue() {
		filter.setHeaderNames("Destination");
		filter.setConvertValues("https://example.com/=http://localhost/");
		
		assertEquals("http://localhost/test/index.html", filter.convertHeaderValue("https://example.com/test/index.html"));
	}

}

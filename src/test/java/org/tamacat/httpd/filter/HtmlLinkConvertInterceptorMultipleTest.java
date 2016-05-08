package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HtmlLinkConvertInterceptorMultipleTest {

	private HtmlLinkConvertInterceptor target;
	
	@Before
	public void setUp() throws Exception {
		target = new HtmlLinkConvertInterceptor();
	}

	@After
	public void tearDown() throws Exception {
	}

	String pattern = "<[^<]*\\s+(href|src|action|.*[0-9]*;?url)=(?:\'|\")?([^('|\")]*)(?:\'|\")?[^>]*>";

	@Test
	public void testSetLinkPattern() {
		assertEquals(0, target.linkPatterns.size());
		
		target.setLinkPattern(pattern);
		assertEquals(1, target.linkPatterns.size());
	}
}

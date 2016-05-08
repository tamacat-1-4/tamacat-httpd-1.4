package org.tamacat.httpd.session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultSessionAttributesTest {

	DefaultSessionAttributes attributes;
	
	@Before
	public void setUp() throws Exception {
		attributes = new DefaultSessionAttributes();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAttribute() {
		assertEquals(null, attributes.getAttribute("key1"));
		assertEquals(null, attributes.remove("key1"));
		
		attributes.setAttribute("key1", "value1");
		assertEquals("value1", attributes.getAttribute("key1"));
		
		attributes.removeAttribute("key1");
		assertEquals(null, attributes.getAttribute("key1"));
	}

	@Test
	public void testGetAttributeKeys() {
		assertEquals(0, attributes.getAttributeKeys().size());
	}

}

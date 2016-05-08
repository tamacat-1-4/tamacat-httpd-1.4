package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequestParametersTest {

	RequestParameters target;

	@Before
	public void setUp() throws Exception {
		target = new RequestParameters();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetParameter() {
		assertNull(target.getParameter("test"));

		target.setParameter("test", (String[])null);
		assertNull(target.getParameter("test"));

		target = new RequestParameters();
		target.setParameter("test", "ok");
		assertEquals("ok", target.getParameter("test"));
	}

	@Test
	public void testGetParameters() {
		assertNull(target.getParameters("test"));

		target.setParameter("test", "1","2","3");
		assertEquals(3, target.getParameters("test").length);
	}

	@Test
	public void testGetParameterNames() {
		assertNotNull(target.getParameterNames());
		assertEquals(0, target.getParameterNames().size());
	}

	@Test
	public void testGetParameterMap() {
		assertNotNull(target.getParameterMap());
		assertEquals(0, target.getParameterMap().size());
	}

}

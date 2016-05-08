package org.tamacat.httpd.session;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultSessionTest {

	DefaultSession session;
	
	@Before
	public void setUp() throws Exception {
		session = new DefaultSession();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testDefaultSessionStringDateDateBoolean() {
		
	}

	@Test
	public void testDefaultSession() {
		session = new DefaultSession();
		//assertEquals(30*60*1000, session.getMaxInactiveInterval());
	}

	@Test
	public void testDefaultSessionInt() {
		session = new DefaultSession(1000);
	}

	@Test
	public void testGetAttribute() {
		session.setAttribute("key1", "value1");
		assertEquals("value1", session.getAttribute("key1"));
		
		session.removeAttribute("key1");
		assertEquals(null, session.getAttribute("key1"));
	}

	@Test
	public void testGetAttributeKeys() {
		session.setAttribute("key1", "value1");
		Set<String> keys = session.getAttributeKeys();
		assertEquals("key1", keys.iterator().next());
	}

	@Test
	public void testGetSessionAttributes() {
		session.setAttribute("key1", "value1");
		SessionAttributes attributes = session.getSessionAttributes();
		assertEquals("value1", attributes.getAttribute("key1"));
		
		session.setSessionAttributes(attributes);
	}

	@Test
	public void testGetCreationDate() {
		assertNotNull(session.getCreationDate());
	}

	@Test
	public void testGetLastAccessDate() {
		session.setLastAccessDate(new Date());
		assertNotNull(session.getLastAccessDate());
	}

	@Test
	public void testGetId() {
		assertNotNull(session.getId());
	}

	@Test
	public void testInvalidate() {
		assertFalse(session.isInvalidate());
		session.invalidate();
		assertTrue(session.isInvalidate());
	}

	@Test
	public void testGetMaxInactiveInterval() {
		session.getMaxInactiveInterval();
	}

	@Test
	public void testUpdateSession() {
		session.updateSession();
	}

	@Test
	public void testSetSessionStore() {
		session.setSessionStore(new FileSessionStore());
	}

}

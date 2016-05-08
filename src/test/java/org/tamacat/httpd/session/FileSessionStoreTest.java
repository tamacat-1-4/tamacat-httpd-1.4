package org.tamacat.httpd.session;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileSessionStoreTest {

	FileSessionStore store;
	
	@Before
	public void setUp() throws Exception {
		store = new FileSessionStore();
		store.setDirectory("./src/test/resources/sessions/");
	}

	@After
	public void tearDown() throws Exception {
		store.release();
	}

	@Test
	public void testSetFileNamePrefix() {
		store.setFileNamePrefix("test_");
		assertEquals("test_", store.fileNamePrefix);
	}

	@Test
	public void testSetFileNameSuffix() {
		store.setFileNameSuffix(".session");
		assertEquals(".session", store.fileNameSuffix);
	}

	@Test
	public void testStore() {
		store.setFileNamePrefix("test_");
		store.setFileNameSuffix(".session");

		Session session = new DefaultSession();
		session.setId("1234567890");
		store.store(session);

		session = store.load("1234567890");
		assertNotNull(session);
		assertEquals("1234567890", session.getId());
		
		assertTrue(new File("./src/test/resources/sessions/test_1234567890.session").exists());
		store.delete("1234567890");
		
		assertFalse(new File("./src/test/resources/sessions/test_1234567890.session").exists());
	}

	@Test
	public void testGetFileNameFilter() {
		store.getFileNameFilter(".session");
	}

	@Test
	public void testGetActiveSessions() {
		assertEquals(0, store.getActiveSessions());
	}

	@Test
	public void testGetActiveSessionIds() {
		assertNull(store.getActiveSessionIds());
	}

	@Test
	public void testRelease() {
		store.release();
	}

}

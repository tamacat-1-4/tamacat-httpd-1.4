package org.tamacat.httpd.session;

import org.junit.Before;
import org.junit.Test;

public class SessionCleanerTest {

	SessionCleaner cleaner;
	@Before
	public void setUp() throws Exception {
		cleaner = new SessionCleaner();
		cleaner.setSessionFactory(SessionManager.getInstance());
	}

	@Test
	public void testStart() {
		Thread t = new Thread(cleaner);
		t.start();
		t.interrupt();
	}

	@Test
	public void testSetCheckInterval() {
		cleaner.setCheckInterval(10);
	}

	@Test
	public void testSetCheckNextSessionIdInterval() {
		cleaner.setCheckNextSessionIdInterval(100);
	}

	@Test
	public void testCheckAndCleanup() {
		cleaner.checkAndCleanup("test");
	}

}

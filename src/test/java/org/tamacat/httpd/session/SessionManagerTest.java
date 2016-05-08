/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SessionManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetSession() throws Exception {
		Session session = SessionManager.getInstance().createSession();
		String id = session.getId();
		Assert.assertNotNull(id);
		session.invalidate();
		//Thread.sleep(1500);
		
		//get the new session.
		Session session2 = SessionManager.getInstance().getSession(id);
		String id2 = session2 != null ? session2.getId() : null;
		Assert.assertNotNull(id2);
	}

	@Test
	public void testRelease() throws Exception {
		Session session = SessionManager.getInstance().createSession();
		String id = session.getId();
		Assert.assertNotNull(id);
		
		SessionManager.getInstance().release();
		
		//get the new session.
		Session session2 = SessionManager.getInstance().getSession(id);
		String id2 = session2 != null ? session2.getId() : null;
		Assert.assertNotNull(id2);
	}
	
//	@Test
//	public void testSerialize() throws Exception {
//		Session session = SessionManager.getInstance().createSession();
//		String id = session.getId();
//		Assert.assertNotNull(id);
//		SessionManager.getInstance().serialize();
//		
//		SessionManager.getInstance().release();
//		
//		SessionManager.getInstance().deserialize(session);
//
//		Session session2 = SessionManager.getInstance().getSession(id);
//		String id2 = session2 != null ? session2.getId() : null;
//		Assert.assertNotNull(id2);
//		Assert.assertEquals(id, id2);
//	}
}

package org.tamacat.httpd.filter.acl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FreeAccessControlTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsFreeAccess() {
		FreeAccessControl acl = new FreeAccessControl("/test/");
		acl.setFreeAccessUrl("/common/");
		acl.setFreeAccessExtensions(".css, .js, .gif");
		
		assertTrue(acl.isFreeAccess("/test/common/test.js"));
		assertTrue(acl.isFreeAccess("/test/common/test.jsp"));
		assertTrue(acl.isFreeAccess("/test/common/"));
		assertTrue(acl.isFreeAccess("/test/test.css"));
		
		assertFalse(acl.isFreeAccess("/test/common"));
		assertFalse(acl.isFreeAccess("/test/"));
		assertFalse(acl.isFreeAccess("."));
		assertFalse(acl.isFreeAccess("/"));
		assertFalse(acl.isFreeAccess(""));
		
		assertFalse(acl.isFreeAccess("/test/test.png"));
		assertFalse(acl.isFreeAccess("/test/test.jsp"));
	}

	@Test
	public void testIsFreeAccessExtensions() {
		FreeAccessControl acl = new FreeAccessControl("/test/");
		acl.setFreeAccessExtensions(".css, .js, .gif");
		
		assertTrue(acl.isFreeAccessExtension("/test/test.css"));
		assertTrue(acl.isFreeAccessExtension("/test/test.js"));
		assertTrue(acl.isFreeAccessExtension("/test/test.gif"));
		
		assertTrue(acl.isFreeAccessExtension("/test/test.css?ver=20150618"));
		assertTrue(acl.isFreeAccessExtension("/test/test.js?ver=1.0"));
		assertTrue(acl.isFreeAccessExtension("/test/test.js?ver=1.0&debug=true&ext=.js"));
		
		assertFalse(acl.isFreeAccessExtension("/test/"));
		assertFalse(acl.isFreeAccessExtension("."));
		assertFalse(acl.isFreeAccessExtension("/"));
		assertFalse(acl.isFreeAccessExtension(""));
		assertFalse(acl.isFreeAccessExtension("/test/test.png"));
		assertFalse(acl.isFreeAccessExtension("/test/test.jsp"));
		assertFalse(acl.isFreeAccessExtension("/test/test.jsp?ver=1.0"));
		assertFalse(acl.isFreeAccessExtension("/test/test.jsp?ver=1.0&debug=true&ext=.jsp"));
	}

	@Test
	public void testIsFreeAccessUrl() {
		FreeAccessControl acl = new FreeAccessControl("/test/");
		acl.setFreeAccessUrl("/common/");
		
		assertTrue(acl.isFreeAccessUrl("/test/common/test.js"));
		assertTrue(acl.isFreeAccessUrl("/test/common/"));

		assertFalse(acl.isFreeAccessUrl("/test/test.css"));
		assertFalse(acl.isFreeAccessUrl("/test/common"));
		assertFalse(acl.isFreeAccessUrl("/test/"));
		assertFalse(acl.isFreeAccessUrl("."));
		assertFalse(acl.isFreeAccessUrl("/"));
		assertFalse(acl.isFreeAccessUrl(""));
	}
	
	@Test
	public void testIsFreeAccessUrl2() {
		FreeAccessControl acl = new FreeAccessControl("/test/");
		acl.setFreeAccessUrl("/common/, /aaa/, /bbb/");
		
		assertTrue(acl.isFreeAccessUrl("/test/common/test.js"));
		assertTrue(acl.isFreeAccessUrl("/test/common/"));

		assertTrue(acl.isFreeAccessUrl("/test/aaa/test.js"));
		assertTrue(acl.isFreeAccessUrl("/test/aaa/common/"));
		assertTrue(acl.isFreeAccessUrl("/test/bbb/common/"));

		
		assertFalse(acl.isFreeAccessUrl("/test/test.css"));
		assertFalse(acl.isFreeAccessUrl("/test/common"));
		assertFalse(acl.isFreeAccessUrl("/test/"));
		assertFalse(acl.isFreeAccessUrl("."));
		assertFalse(acl.isFreeAccessUrl("/"));
		assertFalse(acl.isFreeAccessUrl(""));
		
		assertFalse(acl.isFreeAccessUrl("/aaa/"));
	}
}

package org.tamacat.httpd.exception;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.tamacat.httpd.core.BasicHttpStatus.*;

public class ForbiddenExceptionTest {

	@Test
	public void testForbiddenException() {
		assertEquals(SC_FORBIDDEN, new ForbiddenException().getHttpStatus());
	}

	@Test
	public void testForbiddenExceptionString() {
		ForbiddenException e = new ForbiddenException("TEST ERROR");
		assertEquals(SC_FORBIDDEN, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getMessage());
	}

	@Test
	public void testForbiddenExceptionThrowable() {
		ForbiddenException e = new ForbiddenException(new RuntimeException("TEST ERROR"));
		assertEquals(SC_FORBIDDEN, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getCause().getMessage());
	}

}

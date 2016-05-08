package org.tamacat.httpd.exception;

import static org.junit.Assert.*;
import static org.tamacat.httpd.core.BasicHttpStatus.*;

import org.junit.Test;

public class UnauthorizedExceptionTest {

	@Test
	public void testUnauthorizedException() {
		UnauthorizedException e = new UnauthorizedException();
		assertEquals(SC_UNAUTHORIZED, e.getHttpStatus());
		assertEquals(null, e.getMessage());
	}

	@Test
	public void testUnauthorizedExceptionString() {
		UnauthorizedException e = new UnauthorizedException("TEST ERROR");
		assertEquals(SC_UNAUTHORIZED, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getMessage());
	}

}

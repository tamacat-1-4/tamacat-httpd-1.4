package org.tamacat.httpd.exception;

import static org.junit.Assert.*;
import static org.tamacat.httpd.core.BasicHttpStatus.*;

import org.junit.Test;

public class NotFoundExceptionTest {

	@Test
	public void testNotFoundException() {
		NotFoundException e = new NotFoundException();
		assertEquals(SC_NOT_FOUND, e.getHttpStatus());
	}

	@Test
	public void testNotFoundExceptionString() {
		NotFoundException e = new NotFoundException("TEST ERROR");
		assertEquals(SC_NOT_FOUND, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getMessage());
	}

	@Test
	public void testNotFoundExceptionThrowable() {
		NotFoundException e = new NotFoundException(new RuntimeException("TEST ERROR"));
		assertEquals(SC_NOT_FOUND, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getCause().getMessage());
	}

	@Test
	public void testNotFoundExceptionStringThrowable() {
		NotFoundException e = new NotFoundException("TEST MESSAGE", new RuntimeException("TEST ERROR"));
		assertEquals(SC_NOT_FOUND, e.getHttpStatus());
		assertEquals("TEST MESSAGE", e.getMessage());
		assertEquals("TEST ERROR", e.getCause().getMessage());
	}
}

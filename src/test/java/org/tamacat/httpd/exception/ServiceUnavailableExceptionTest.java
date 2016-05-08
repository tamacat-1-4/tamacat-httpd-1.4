package org.tamacat.httpd.exception;

import static org.junit.Assert.*;
import static org.tamacat.httpd.core.BasicHttpStatus.*;

import org.junit.Test;

public class ServiceUnavailableExceptionTest {

	@Test
	public void testServiceUnavailableException() {
		ServiceUnavailableException e = new ServiceUnavailableException();
		assertEquals(SC_SERVICE_UNAVAILABLE, e.getHttpStatus());
		assertEquals(null, e.getMessage());
	}

	@Test
	public void testServiceUnavailableExceptionThrowable() {
		ServiceUnavailableException e = new ServiceUnavailableException(new RuntimeException("TEST ERROR"));
		assertEquals(SC_SERVICE_UNAVAILABLE, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getCause().getMessage());
	}

	@Test
	public void testServiceUnavailableExceptionString() {
		ServiceUnavailableException e = new ServiceUnavailableException("TEST ERROR");
		assertEquals(SC_SERVICE_UNAVAILABLE, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getMessage());
	}

	@Test
	public void testServiceUnavailableExceptionStringThrowable() {
		ServiceUnavailableException e = new ServiceUnavailableException("TEST MESSAGE", new RuntimeException("TEST ERROR"));
		assertEquals(SC_SERVICE_UNAVAILABLE, e.getHttpStatus());
		assertEquals("TEST ERROR", e.getCause().getMessage());
	}

}

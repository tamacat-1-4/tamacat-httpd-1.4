package org.tamacat.httpd.exception;

import static org.junit.Assert.*;
import org.junit.Test;
import org.tamacat.httpd.core.BasicHttpStatus;

public class HttpStatusTest {

	@Test
	public void testGetHttpStatus() {
		assertEquals(BasicHttpStatus.SC_OK, BasicHttpStatus.getHttpStatus(200));
		assertEquals(BasicHttpStatus.SC_NOT_FOUND, BasicHttpStatus.getHttpStatus(404));
		assertEquals(BasicHttpStatus.SC_INTERNAL_SERVER_ERROR, BasicHttpStatus.getHttpStatus(500));
		assertEquals(BasicHttpStatus.SC_UNKNOWN, BasicHttpStatus.getHttpStatus(999));
	}

	@Test
	public void testGetStatusCode() {
		assertEquals(200, BasicHttpStatus.SC_OK.getStatusCode());
		assertEquals(404, BasicHttpStatus.SC_NOT_FOUND.getStatusCode());
	}

	@Test
	public void testGetReasonPhrase() {
		assertEquals("OK", BasicHttpStatus.SC_OK.getReasonPhrase());
	}

	@Test
	public void testIsInformational() {
		assertEquals(true, BasicHttpStatus.SC_CONTINUE.isInformational());
		assertEquals(false, BasicHttpStatus.SC_NOT_FOUND.isInformational());
		assertEquals(false, BasicHttpStatus.SC_OK.isInformational());
	}

	@Test
	public void testIsSuccess() {
		assertEquals(false, BasicHttpStatus.SC_CONTINUE.isSuccess());
		assertEquals(true, BasicHttpStatus.SC_OK.isSuccess());
		assertEquals(false, BasicHttpStatus.SC_MULTIPLE_CHOICES.isSuccess());
	}

	@Test
	public void testIsRedirection() {
		assertEquals(true, BasicHttpStatus.SC_MULTIPLE_CHOICES.isRedirection());
		assertEquals(false, BasicHttpStatus.SC_OK.isRedirection());
		assertEquals(false, BasicHttpStatus.SC_BAD_REQUEST.isRedirection());
	}

	@Test
	public void testIsClientError() {
		assertEquals(true, BasicHttpStatus.SC_BAD_REQUEST.isClientError());
		assertEquals(false, BasicHttpStatus.SC_MULTIPLE_CHOICES.isClientError());
		assertEquals(false, BasicHttpStatus.SC_INTERNAL_SERVER_ERROR.isClientError());
	}

	@Test
	public void testIsServerError() {
		assertEquals(true, BasicHttpStatus.SC_INTERNAL_SERVER_ERROR.isServerError());
		assertEquals(true, BasicHttpStatus.SC_SERVICE_UNAVAILABLE.isServerError());
		assertEquals(false, BasicHttpStatus.SC_BAD_REQUEST.isServerError());
		assertEquals(false, BasicHttpStatus.SC_NOT_FOUND.isServerError());
	}
}

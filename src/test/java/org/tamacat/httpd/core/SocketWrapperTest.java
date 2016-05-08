package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.DummySocket;

public class SocketWrapperTest {

	SocketWrapper wrapper;

	@Before
	public void setUp() throws Exception {
		Socket socket = new DummySocket();
		wrapper = new SocketWrapper(socket);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsWebDAVSupport() {
		assertFalse(wrapper.isWebDAVSupport());
		wrapper.setWebDAVSupport(true);
		assertTrue(wrapper.isWebDAVSupport());
	}

	@Test
	public void testIsWebSocketSupport() {
		assertFalse(wrapper.isWebSocketSupport());
		wrapper.setWebSocketSupport(true);
		assertTrue(wrapper.isWebSocketSupport());
	}

	@Test
	public void testGetSocket() {
		assertNotNull(wrapper.getSocket());
	}

}

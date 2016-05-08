package org.tamacat.httpd.core.jmx;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.core.jmx.BasicCounter;

public class BasicCounterTest {

	BasicCounter counter;

	@Before
	public void setUp() throws Exception {
		counter = new BasicCounter();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetPath() {
		counter.setPath("test");
		assertEquals("test", counter.getPath());
	}

	@Test
	public void testGetActiveConnections() {
		assertSame(0, counter.getActiveConnections());
		counter.countUp();
		assertSame(1, counter.getActiveConnections());
		counter.countDown();
		assertSame(0, counter.getActiveConnections());
	}

	@Test
	public void testGetAccessCount() {
		assertSame(0L, counter.getAccessCount());
		counter.access();
		assertSame(1L, counter.getAccessCount());
		counter.access();
		assertSame(2L, counter.getAccessCount());
		counter.access();
		assertSame(3L, counter.getAccessCount());
		counter.resetAccessCount();
		assertSame(0L, counter.getAccessCount());
	}

	@Test
	public void testGetErrorCount() {
		assertSame(0L, counter.getErrorCount());
		counter.error();
		assertSame(1L, counter.getErrorCount());
		counter.error();
		assertSame(2L, counter.getErrorCount());
		counter.error();
		assertSame(3L, counter.getErrorCount());
		counter.resetErrorCount();
		assertSame(0L, counter.getErrorCount());
	}

	@Test
	public void testGetStartedTime() {
		assertNotNull(counter.getStartedTime());
	}

	@Test
	public void testGetAverageResponseTime() {
		assertTrue(0 == counter.getAverageResponseTime());

		counter.setResponseTime(1000);
		counter.setResponseTime(2000);
		counter.setResponseTime(3000);
		assertTrue(2000 == counter.getAverageResponseTime());
	}

	@Test
	public void testGetMaximumResponseTime() {
		assertTrue(0 == counter.getMaximumResponseTime());

		counter.setResponseTime(1000);
		counter.setResponseTime(2000);
		counter.setResponseTime(3000);
		assertTrue(3000 == counter.getMaximumResponseTime());
	}

	@Test
	public void testRegister() {
		//counter.register();
	}
}

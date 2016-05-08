package org.tamacat.httpd.core.jmx;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.core.jmx.URLBasicCounter;

public class URLBasicCounterTest {
	
	URLBasicCounter counter;
	
	@Before
	public void setUp() throws Exception {
		counter = new URLBasicCounter();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCounter() {
		assertNull(counter.getCounter("test"));
		
		counter.register("test");
		assertNotNull(counter.getCounter("test"));
	}

	@Test
	public void testSetObjectName() {
		counter.setObjectName("org.tamacat.httpd:type=URL#");
	}

}

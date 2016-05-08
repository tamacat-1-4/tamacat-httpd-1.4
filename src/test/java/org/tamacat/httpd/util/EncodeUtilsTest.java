/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EncodeUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetJavaEncoding() {
		assertEquals("UTF-8", EncodeUtils.getJavaEncoding("utf-8"));
		assertEquals("MS932", EncodeUtils.getJavaEncoding("Shift_JIS"));
		assertEquals("EUC_JP", EncodeUtils.getJavaEncoding("euc-jp"));
		assertEquals("ISO2022JP", EncodeUtils.getJavaEncoding("iso-2022-jp"));
	}

}

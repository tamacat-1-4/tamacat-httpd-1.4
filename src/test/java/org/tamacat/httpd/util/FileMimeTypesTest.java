/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.util.PropertyUtils;

public class FileMimeTypesTest extends TestCase {

	static final Properties mimeTypes;
    static {
    	mimeTypes = PropertyUtils.getProperties("org/tamacat/httpd/mime-types.properties");
    }
    
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
    protected String getContentType(File file) {
    	String fileName = file != null ? file.getName() : "";
    	String ext = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
    	return mimeTypes.getProperty(ext.toLowerCase(), "text/html");
    }
	
    @Test
	public void testType() {
		//File file = new File("c:/tmp/test.jpg");
		//String type = getContentType(file);
		//assertEquals("image/jpeg", type);
	}

}

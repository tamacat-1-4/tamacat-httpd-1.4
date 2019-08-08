/*
 * Copyright (c) 2014-2019 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.Properties;

import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * Properties file in CLASSPATH
 * - org/tamacat/httpd/mime-types.properties
 * - mime-types.properties
 * hash data (key:file extention, value:content-type)
 */
public class MimeUtils {
	private static Properties mimeTypes;

	static {
		mimeTypes = PropertyUtils.marge(
				"org/tamacat/httpd/mime-types.properties",
				"mime-types.properties");
	}

	/**
	 * Get a content-type from mime-types.properties.
	 * content-type was unknown then returns null.
	 * @param path
	 * @return
	 */
	public static String getContentType(String path) {
		if (StringUtils.isEmpty(path)) return null;
		if (path.indexOf('?')>=0) {
			String[] tmp = StringUtils.split(path, "?");
			if (tmp.length >= 1) {
				path = tmp[0];
			}
		}
		String ext = path.substring(path.lastIndexOf('.') + 1, path.length());
		String contentType = mimeTypes.getProperty(ext.toLowerCase());
		return contentType;
	}
}

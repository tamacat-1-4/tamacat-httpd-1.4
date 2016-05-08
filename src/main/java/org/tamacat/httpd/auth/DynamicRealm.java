/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tamacat.util.DateUtils;

/**
 * <p>
 * This class is dynamic and make realm based on a date.
 * 
 * <pre>
 * Example,<code>
 * Realm-${yyyyMMdd} -> Realm-20090101
 * </code>
 * </pre>
 */
public class DynamicRealm {

	static final Pattern VARIABLE_PATTERN = Pattern.compile("(.*\\$\\{)(.*)(\\}.*)");

	static final IllegalArgumentException REALM_IS_NULL_EXCEPTION = new IllegalArgumentException();

	/**
	 * <p>
	 * Substitute a variable part of realm on a date.<br>
	 * The following pattern letters are defined
	 * {@link java.text.SimpleDateFormat}.
	 * </p>
	 * <p>
	 * Replace holder examples,<br>
	 * <ul>
	 * <li>${yyyyMMdd}</li>
	 * <li>${yyyyMMddHHmmssS}</li>
	 * </ul>
	 * 
	 * @param realm
	 *            if realm is null, then throws IllegalArgumentException.
	 * @param date
	 * @return replace realm's ${yyyyMMdd} pattern with date.
	 */
	public static String getRealm(String realm, Date date) {
		if (realm == null) {
			throw REALM_IS_NULL_EXCEPTION;
		}
		Matcher matcher = VARIABLE_PATTERN.matcher(realm);
		if (matcher.find()) {
			String pattern = matcher.group(2);
			if (pattern != null) {
				return realm.replace("${" + pattern + "}", DateUtils.getTime(date, pattern));
			}
		}
		return realm;
	}
}

/*
 * Copyright (c) 2013, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tamacat.util.StringUtils;

public class Digest {
	private Map<String, String> params = new LinkedHashMap<>();

	public Digest(String line) {
		String[] params = StringUtils.split(line, ",");
		for (String keyValue : params) {
			String[] param = StringUtils.split(keyValue.trim(), "=");
			if (param != null && param.length >= 2) {
				String key = param[0].trim();
				StringBuilder value = new StringBuilder(param[1]);
				if (param.length > 2) {
					for (int i = 2; i < param.length; i++) {
						value.append("=" + param[i]);
					}
				}
				setParam(key, value.toString().replaceFirst("^\"", "").replaceFirst("\"$", ""));
			}
		}
	}

	public Digest() {}

	public void setParam(String key, String value) {
		this.params.put(key, value);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public String getUsername() {
		return params.get("username");
	}

	public String getRealm() {
		return params.get("realm");
	}

	public String getNonce() {
		return params.get("nonce");
	}

	public String getUri() {
		return params.get("uri");
	}

	public String getAlgorithm() {
		return params.get("algorithm");
	}

	public String getResponse() {
		return params.get("response");
	}

	public String getQop() {
		return params.get("qop");
	}

	public String getNc() {
		return params.get("nc");
	}

	public String getCnonce() {
		return params.get("cnonce");
	}
}

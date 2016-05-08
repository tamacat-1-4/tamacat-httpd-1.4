/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestParameters {

	private Map<String, List<String>> params = new LinkedHashMap<>();

	public void setParameter(String name, String... values) {
		if (name != null) {
			List<String> valueList = getValueList(name);
			if (values == null) {
				valueList.add(null);
				params.put(name, valueList);
			} else if (values.length == 1) {
				valueList.add(values[0]);
				params.put(name, valueList);
			} else {
				for (String value : values) {
					valueList.add(value);
					params.put(name, valueList);
				}
			}
		}
	}

	public String getParameter(String name) {
		List<String> values = getValueList(name);
		int size = values.size();
		return size > 0 ? values.get(0) : null;
	}

	public String[] getParameters(String name) {
		List<String> values = getValueList(name);
		int size = values.size();
		return size > 0 ? values.toArray(new String[size]) : null;
	}

	public Set<String> getParameterNames() {
		return params.keySet();
	}

	public Map<String, List<String>> getParameterMap() {
		return params;
	}

	private List<String> getValueList(String name) {
		List<String> valueList = params.get(name);
		if (valueList == null) {
			valueList = new ArrayList<String>();
		}
		return valueList;
	}
}

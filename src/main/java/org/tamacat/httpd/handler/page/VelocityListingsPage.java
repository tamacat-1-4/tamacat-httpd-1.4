/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler.page;

import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.DateUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>It is the directory listings page that used Velocity template.
 */
public class VelocityListingsPage {

	static final Log LOG = LogFactory.getLog(VelocityListingsPage.class);

	protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	protected static final String DEFAULT_ERROR_500_HTML
		= "<html><body><p>500 Internal Server Error.<br /></p></body></html>";
	protected String listingsPage = "listings";
	protected VelocityEngine velocityEngine;

	protected String encoding;
	protected boolean useSearch;
	protected String dateFormat = "yyyy-MM-dd HH:mm";

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setLocale(String str) {
		if (StringUtils.isNotEmpty(str)) {
			this.locale = StringUtils.getLocale(str);
		}
	}

	protected Locale locale = Locale.getDefault();

	/**
	 * @since 1.1
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setListingsPage(String listingsPage) {
		this.listingsPage = listingsPage;
	}

	public VelocityListingsPage(Properties props) {
		try {
			velocityEngine = new VelocityEngine();
			velocityEngine.setProperty("resource.loader", "list");
			velocityEngine.init(props);
			if ("true".equalsIgnoreCase(props.getProperty("list.resource.search","false"))) {
				useSearch = true;
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	public String getListingsPage(
			HttpRequest request, HttpResponse response,
			File file) {
		VelocityContext context = new VelocityContext();
		return getListingsPage(request, response, context, file);
	}

	public String getListingsPage(
			HttpRequest request, HttpResponse response,
			VelocityContext context, File file) {
		try {
			context.put("url", URLDecoder.decode(RequestUtils.getPath(request),"UTF-8"));
		} catch (Exception e) {
			context.put("url", RequestUtils.getPath(request));
		}

		if (request.getRequestLine().getUri().lastIndexOf('/') >= 0) {
			context.put("parent", "../");
		}
		final String q = useSearch? getParameter(request, "q"): "";
		context.put("q", q);
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (StringUtils.isNotEmpty(q)) {
					return pathname.getName().indexOf(q)>=0;
				} else {
					return ! pathname.isHidden()
						&& ! pathname.getName().startsWith(".");
				}
			}
		});
		ArrayList<Map<String, String>> list = new ArrayList<>();
		if (files != null) {
			Arrays.sort(files, new FileSort());
			for (File f : files) {
				Map<String, String> map = new HashMap<>();
				String name = StringUtils.isNotEmpty(encoding)? StringUtils.encode(f.getName(),"UTF-8") : f.getName();
				if (f.isDirectory()) {
					map.put("getName", name + "/");
					map.put("length", "-");
				} else {
					map.put("getName", name);
					map.put("length", String.format("%1$,3d KB", (long)Math.ceil(f.length()/1024d)).trim());
				}
				map.put("isDirectory", String.valueOf(f.isDirectory()));
				map.put("lastModified", DateUtils.getTime(new Date(f.lastModified()), dateFormat, locale));
				list.add(map);
			}
		}
		
		context.put("list", list);
		try {
			Template template = getTemplate(listingsPage + ".vm");
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			return writer.toString();
		} catch (Exception e) {
			LOG.trace(e.getMessage());
			return DEFAULT_ERROR_500_HTML;
		}
	}

	protected Template getTemplate(String page) throws Exception {
		return velocityEngine.getTemplate("templates/" + page, "UTF-8");
	}

	static class FileSort implements Comparator<File> {
		public int compare(File src, File target) {
			if (src.isDirectory() && target.isFile()) return -1;
			if (src.isFile() && target.isDirectory()) return 1;
			int diff = src.getName().compareTo(target.getName());
			return diff;
		}
	}

	String getParameter(HttpRequest request, String name) {
		String path = request.getRequestLine().getUri();
		if (path.indexOf('?') >= 0) {
			String[] requestParams = path.split("\\?");
			if (requestParams.length >= 2) {
				String params = requestParams[1];
				String[] param = params.split("&");
				for (String kv : param) {
					String[] p = kv.split("=");
					if (p.length >=2 && p[0].equals(name)) {
						try {
							return URLDecoder.decode(p[1], "UTF-8");
						} catch (Exception e) {
							LOG.warn(e.getMessage());
						}
					}
				}
			}
		}
		return null;
	}
}

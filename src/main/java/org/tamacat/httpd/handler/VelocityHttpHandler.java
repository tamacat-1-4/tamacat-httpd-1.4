/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.handler.page.VelocityListingsPage;
import org.tamacat.httpd.handler.page.VelocityPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.util.PropertyUtils;

/**
 * <p>
 * It is implements of {@link HttpHandler} that uses {@code Apache Velocity}.
 */
public class VelocityHttpHandler extends AbstractHttpHandler {

	public static final String CONTENT_TYPE = "ResponseHeader__ContentType__";
	protected String welcomeFile = "index";
	protected boolean listings;

	protected VelocityListingsPage listingPage;
	protected VelocityPage page;
	protected final Set<String> urlPatterns = new HashSet<>();

	public void setUrlPatterns(String patterns) {
		for (String pattern : patterns.split(",")) {
			urlPatterns.add(pattern.trim());
		}
	}

	public boolean isMatchUrlPattern(String path) {
		if (urlPatterns.size() > 0) {
			for (String pattern : urlPatterns) {
				if (pattern.endsWith("/") && path.matches(pattern)) {
					return true;
				} else if (path.lastIndexOf(pattern) >= 0) {
					return true;
				}
			}
		} else if (path.lastIndexOf(".html") >= 0) {
			return true;
		}
		return false;
	}

	@Override
	public void setDocsRoot(String docsRoot) {
		super.setDocsRoot(docsRoot);
		if (page != null) {
			page.init(docsRoot);
		}
	}

	@Override
	public void setServiceUrl(ServiceUrl serviceUrl) {
		super.setServiceUrl(serviceUrl);
		Properties props = PropertyUtils.getProperties("velocity.properties", getClassLoader());
		listingPage = new VelocityListingsPage(props);
		page = new VelocityPage(props);
		if (docsRoot != null) {
			page.init(docsRoot);
		}
	}

	/**
	 * <p>
	 * Set the welcome file. This method use after {@link #setListings}.
	 * 
	 * @param welcomeFile
	 */
	public void setWelcomeFile(String welcomeFile) {
		this.welcomeFile = welcomeFile;
	}

	/**
	 * <p>
	 * Should directory listings be produced if there is no welcome file in this
	 * directory.
	 * </p>
	 *
	 * <p>
	 * The welcome file becomes unestablished when I set true.<br>
	 * When I set the welcome file, please set it after having carried out this
	 * method.
	 * </p>
	 *
	 * @param listings
	 *            true: directory listings be produced (if welcomeFile is null).
	 */
	public void setListings(boolean listings) {
		this.listings = listings;
		if (listings) {
			this.welcomeFile = null;
		}
	}

	public void setListingsPage(String listingsPage) {
		listingPage.setListingsPage(listingsPage);
	}

	protected boolean useDirectoryListings() {
		if (listings) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void doRequest(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException,
			IOException {
		VelocityContext ctx = (VelocityContext) context.getAttribute(VelocityContext.class.getName());
		if (ctx == null) {
			ctx = new VelocityContext();
		}
		String path = RequestUtils.getPath(request);
		ctx.put("param", RequestUtils.parseParameters(request, context, encoding).getParameterMap());
		ctx.put("contextRoot", serviceUrl.getPath().replaceFirst("/$", ""));
		if (isMatchUrlPattern(path)) {
			// delete the extention of file name. (index.html -> index)
			String file = path.indexOf(".") >= 0 ? path.split("\\.")[0] : path;
			setEntity(request, response, ctx, file);
		} else if (path.endsWith("/")) {
			// directory -> index page.
			File file = null;
			if (path.endsWith("/")) {
				if (welcomeFile == null) {
					welcomeFile = "index.vm";
				}
				file = new File(docsRoot + getDecodeUri(path + welcomeFile));
			}
			if (useDirectoryListings() && file.canRead() == false) {
				file = new File(docsRoot + getDecodeUri(path));
				setListFileEntity(request, response, file);
			} else {
				setEntity(request, response, ctx, path + "index");
			}
		} else {
			// get the file in this server.
			setFileEntity(request, response, path);
		}
	}

	protected void setListFileEntity(HttpRequest request, HttpResponse response, File file) {
		try {
			String html = listingPage.getListingsPage(request, response, file);
			if (!"HEAD".equals(request.getRequestLine().getMethod())) {
				response.setEntity(getEntity(html));
			}
			response.setStatusCode(BasicHttpStatus.SC_OK.getStatusCode());
			response.setReasonPhrase(BasicHttpStatus.SC_OK.getReasonPhrase());
		} catch (Exception e) {
			throw new NotFoundException(e);
		}
	}

	protected void setEntity(HttpRequest request, HttpResponse response, VelocityContext ctx, String path) {
		// Do not set an entity when it already exists.
		if (response.getEntity() == null) {
			String html = page.getPage(request, response, ctx, path);
			Object contentType = ctx.get(CONTENT_TYPE);
			if (!"HEAD".equals(request.getRequestLine().getMethod())) {
				if (contentType != null && contentType instanceof String) {
					response.setEntity(getEntity(html, (String) contentType));
				} else {
					response.setEntity(getEntity(html));
				}
			}
		}
	}

	protected void setFileEntity(HttpRequest request, HttpResponse response, String path) {
		// Do not set an entity when it already exists.
		if (response.getEntity() == null) {
			try {
				File file = new File(docsRoot + getDecodeUri(path));// r.toURI());
				if (file.isDirectory() || !file.exists() || !file.canRead()) {
					throw new NotFoundException(path + " is not found this server.");
				}
				if (!"HEAD".equals(request.getRequestLine().getMethod())) {
					response.setEntity(getFileEntity(file));
				}
			} catch (HttpException e) {
				throw e;
			} catch (Exception e) {
				throw new NotFoundException(e);
			}
		}
	}

	protected HttpEntity getEntity(String html, String contentType) {
		try {
			StringEntity entity = new StringEntity(html, encoding);
			entity.setContentType(contentType);
			return entity;
		} catch (Exception e1) {
			return null;
		}
	}

	@Override
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html, encoding);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (Exception e1) {
			return null;
		}
	}

	protected HttpEntity getFileEntity(File file, String contentType) {
		FileEntity body = new FileEntity(file, ContentType.create(contentType, encoding));
		return body;
	}

	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, ContentType.create(getContentType(file)));
		return body;
	}
}

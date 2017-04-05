/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.handler.page.VelocityListingsPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;

/**
 * <p>The {@link HttpHandler} for local file access.
 */
public class LocalFileHttpHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(LocalFileHttpHandler.class);

	protected String welcomeFile = "index.html";
	protected VelocityListingsPage listingPage;
	protected boolean listings;
	protected Properties props;

	@Override
	public void setServiceUrl(ServiceUrl serviceUrl) {
		super.setServiceUrl(serviceUrl);
		props = PropertyUtils.getProperties("velocity.properties", getClassLoader());
		listingPage = new VelocityListingsPage(props);
	}

	/**
	 * <p>Set the welcome file.
	 * This method use after {@link #setListings}.
	 * @param welcomeFile
	 */
	public void setWelcomeFile(String welcomeFile) {
		this.welcomeFile = welcomeFile;
	}

	/**
	 * <p>Should directory listings be produced
	 * if there is no welcome file in this directory.</p>
	 *
	 * <p>The welcome file becomes unestablished when I set true.<br>
	 * When I set the welcome file, please set it after having
	 * carried out this method.</p>
	 *
	 * @param listings true: directory listings be produced (if welcomeFile is null).
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
		if (listings && welcomeFile == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void doRequest(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		String path = RequestUtils.getPath(request);
		if (path.endsWith("/") && useDirectoryListings() == false) {
			path = path + welcomeFile;
		}
		File file = new File(docsRoot, getDecodeUri(path.replace(serviceUrl.getPath(), "/")));
		///// 404 NOT FOUND /////
		if (!file.exists()) {
			LOG.debug("File " + file.getPath() + " not found");
			throw new NotFoundException();
		}
		///// 403 FORBIDDEN /////
		else if (!file.canRead() || file.isDirectory()) {
			if (file.isDirectory() && useDirectoryListings()) {
				String html = listingPage.getListingsPage(
						request, response, file);
				response.setStatusCode(BasicHttpStatus.SC_OK.getStatusCode());
				response.setReasonPhrase(BasicHttpStatus.SC_OK.getReasonPhrase());
				response.setEntity(getEntity(html));
			} else {
				LOG.trace("Cannot read file " + file.getPath());
				throw new ForbiddenException();
			}
		}
		///// 200 OK /////
		else {
			LOG.trace("File " + file.getPath() + " found");
			response.setStatusCode(BasicHttpStatus.SC_OK.getStatusCode());
			response.setReasonPhrase(BasicHttpStatus.SC_OK.getReasonPhrase());
			if (!"HEAD".equals(request.getRequestLine().getMethod())) {
				response.setEntity(getFileEntity(file));
			}
			LOG.trace("Serving file " + file.getPath());
		}
	}

	@Override
	protected HttpEntity getEntity(String html) {
		StringEntity body = null;
		try {
			body = new StringEntity(html, encoding);
			body.setContentType(DEFAULT_CONTENT_TYPE);
		} catch (Exception e) {
		}
		return body;
	}

	@Override
	protected HttpEntity getFileEntity(File file) {
		ContentType contentType = ContentType.DEFAULT_TEXT;
		try {
			contentType = ContentType.create(getContentType(file));
		} catch (Exception e) {
		}
		FileEntity body = new FileEntity(file, contentType);
		return body;
	}
}

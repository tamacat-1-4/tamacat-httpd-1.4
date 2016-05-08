/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class DebugSessionListener implements SessionListener {

	static Log LOG = LogFactory.getLog(DebugSessionListener.class);
	
	@Override
	public void sessionCreated(Session session) {
		LOG.info("created: " + session.getId());
	}

	@Override
	public void sessionDestroyed(Session session) {
		LOG.info("destroyed: " + session.getId());
	}

}

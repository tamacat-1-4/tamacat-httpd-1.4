/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.util.Set;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;

public class SessionCleaner implements Runnable {

	static final Log LOG = LogFactory.getLog(SessionCleaner.class);

	private int checkInterval =  30 * 1000; //default 30sec.
	private int checkNextSessionIdInterval = 50; //default 50ms.
	private String name = "Cleaner";

	private SessionFactory manager;

	public void setSessionFactory(SessionFactory manager) {
		this.manager = manager;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCheckInterval(int checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setCheckNextSessionIdInterval(int checkNextSessionIdInterval) {
		this.checkNextSessionIdInterval = checkNextSessionIdInterval;
	}

	@Override
	public void run() {
		try {
			while (true) {
				LOG.trace("clean check.");
				Set<String> ids = manager.getActiveSessionIds();
				if (ids != null) {
					for (String id : ids) {
						checkAndCleanup(id);
						Thread.sleep(checkNextSessionIdInterval); //wait next session id check.
					}
				}
				Thread.sleep(checkInterval);
			}
		} catch (InterruptedException e) {
			LOG.debug(e.getMessage());
			LOG.warn("stop.");
		}
	}

	void checkAndCleanup(String id) {
		if (id != null) {
			try {
				Session session = manager.checkSession(id);
				if (session == null) LOG.debug("cleanup: " + id);
			} catch (Exception e) {
				LOG.warn(e.getMessage());
				LOG.debug(ExceptionUtils.getStackTrace(e));
			}
		}
	}

}

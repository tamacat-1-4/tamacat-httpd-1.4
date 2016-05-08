/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class MemorySessionStore implements SessionStore {

	static final Log LOG = LogFactory.getLog(MemorySessionStore.class);
	static final Map<String, Session> STORE = new ConcurrentHashMap<>();
	
	@Override
	public synchronized void store(Session session) {
		STORE.put(session.getId(), session);
	}

	@Override
	public synchronized Session load(String id) {
		return STORE.get(id);
	}
	
	@Override
	public synchronized void delete(String id) {
		STORE.remove(id);
	}

	@Override
	public synchronized void release() {
		STORE.clear();
	}

	@Override
	public int getActiveSessions() {
		return STORE.size();
	}

	@Override
	public Set<String> getActiveSessionIds() {
		return STORE.keySet();
	}
}

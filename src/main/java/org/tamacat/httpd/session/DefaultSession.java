/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.util.Date;
import java.util.Set;

import org.tamacat.util.UniqueCodeGenerator;

public class DefaultSession implements Session, SessionSerializable {

	private static final long serialVersionUID = 1915915524691987130L;
	
	private Date creationDate;
	private Date lastAccessDate;
	private String id;
	private SessionAttributes attributes;
	private int maxInactiveInterval; // = 30 * 60 * 1000; //30min.
	private transient SessionStore sessionStore;
	private boolean invalidate;
	
	public DefaultSession(String id, Date createDate, Date lastAccessDate, boolean invalidate) {
		this.id = id;
		this.creationDate = createDate;
		this.lastAccessDate = lastAccessDate;
		this.attributes = new DefaultSessionAttributes();
		this.invalidate = invalidate;
	}
	
	public DefaultSession() {
		this(30*60*1000);
	}
	
	public DefaultSession(int maxInactiveInterval) {
		this.creationDate = new Date();
		this.attributes = new DefaultSessionAttributes();
		this.id = UniqueCodeGenerator.generate();
		this.maxInactiveInterval = maxInactiveInterval;
		updateSession();
	}
	
	@Override
	public Object getAttribute(String key) {
		return attributes.getAttribute(key);
	}
	
	@Override
	public void setAttribute(String key, Object value) {
		attributes.setAttribute(key, value);
		updateSession();
	}
	
	@Override
	public void removeAttribute(String key) {
		attributes.removeAttribute(key);
		updateSession();
	}

	@Override
	public Set<String> getAttributeKeys() {
		return attributes.getAttributeKeys();
	}

	@Override
	public void setSessionAttributes(SessionAttributes attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public SessionAttributes getSessionAttributes() {
		return attributes;
	}
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	
	@Override
	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void invalidate() {
		attributes.clear();
		invalidate = true;
		//updateSession();
		SessionManager.getInstance().invalidate(this);
	}
	
	public boolean isInvalidate() {
		return invalidate;
	}
	
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
	
	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}
	
	@Override
	public void updateSession() {
		if (invalidate) {
			if (sessionStore != null) sessionStore.delete(id);
		} else {
			lastAccessDate = new Date();
			if (sessionStore != null) sessionStore.store(this);
		}
	}
	
	@Override
	public void setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}
	
	private void writeObject(java.io.ObjectOutputStream stream)
			throws java.io.IOException {
		stream.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws java.io.IOException, ClassNotFoundException {
		stream.defaultReadObject();
	}
}

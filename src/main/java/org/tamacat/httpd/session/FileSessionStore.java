/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.tamacat.io.RuntimeIOException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class FileSessionStore implements SessionStore {

	static final Log LOG = LogFactory.getLog(FileSessionStore.class);

	String directory = "./";
	String fileNamePrefix = "";
	String fileNameSuffix = ".ser";
	AtomicInteger count = new AtomicInteger(0);

	public void setDirectory(String directory) {
		if (! directory.endsWith("/")) directory = directory + "/";
		this.directory = directory;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public void setFileNameSuffix(String fileNameSuffix) {
		this.fileNameSuffix = fileNameSuffix;
	}

	@Override
	public void store(Session session) {
		synchronized (session) {
			String name = directory + fileNamePrefix + session.getId() + fileNameSuffix;
			try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(name))) {
				out.writeObject(session);
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		}
	}

	@Override
	public Session load(String id) {
		String fileName = directory + fileNamePrefix + id + fileNameSuffix;
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
			Session loaded = (Session) in.readObject();
			if (loaded != null) {
				return loaded;
			}
		} catch (IOException | ClassNotFoundException e) {
			LOG.warn(e.getMessage());
		}
		return null;
	}

	@Override
	public void delete(String id) {
		String fileName = directory + fileNamePrefix + id + fileNameSuffix;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	FilenameFilter getFileNameFilter(final String fileNameSuffix) {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(fileNameSuffix);
			}
		};
	}

	@Override
	public int getActiveSessions() {
		return count.get();
	}

	@Override
	public Set<String> getActiveSessionIds() {
		return null;
	}

	@Override
	public void release() {
		String[] files = new File(directory).list(
				getFileNameFilter(fileNameSuffix));
		for (String f : files) {
			try {
				File file = new File(f);
				file.deleteOnExit();
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
		}
	}
}

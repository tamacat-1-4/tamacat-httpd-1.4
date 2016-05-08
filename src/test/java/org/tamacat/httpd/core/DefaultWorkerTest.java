package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.HttpService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.handler.DefaultHttpService;
import org.tamacat.httpd.mock.DummySocket;
import org.tamacat.io.RuntimeIOException;

public class DefaultWorkerTest {

	DefaultWorker worker;

	@Before
	public void setUp() throws Exception {
		HttpService httpService = new DefaultHttpService(
				new HttpProcessorBuilder(),
				new KeepAliveConnReuseStrategy(),
				new DefaultHttpResponseFactory(), null, null);
		worker = new DefaultWorker();
		worker.setServerConfig(new ServerConfig());
		worker.setSocket(new DummySocket());
		worker.setHttpService(httpService);
	}

	@After
	public void tearDown() throws Exception {
		worker.shutdown(worker.conn);
	}

	@Test
	public void testRun() {
		new Thread(worker).start();
	}

	@Test
	public void testHandleException() {
		worker.handleException(new SSLHandshakeException("test"));
		worker.handleException(new SocketException("test"));
		worker.handleException(new ConnectionClosedException("test"));
		worker.handleException(new SocketTimeoutException("test"));
		worker.handleException(new RuntimeIOException("test"));
	}

	@Test
	public void testIsClosed() throws Exception {
		assertFalse(worker.isClosed());

		//worker.shutdown(
				worker.conn.close();//);
		//assertTrue(worker.isClosed());
	}
}

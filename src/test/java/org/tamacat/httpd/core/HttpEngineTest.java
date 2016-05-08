package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.ssl.DefaultSSLContextCreator;
import org.tamacat.util.PropertyUtils;

public class HttpEngineTest {

	HttpEngine engine;

	@Before
	public void setUp() throws Exception {
		engine = new HttpEngine();
		engine.setWorkerExecutor(new DefaultWorkerExecutor());
	}

	@After
	public void tearDown() throws Exception {
		engine.stopHttpd();
	}

	@Test
	public void testInit() {
		engine.init();
	}

	@Test
	public void testStartHttpd() {

	}

	@Test
	public void testStopHttpd() {

	}

	@Test
	public void testRestartHttpd() {

	}

	@Test
	public void testCreateSecureServerSocket() throws IOException {
		ServerConfig serverConfig = new ServerConfig(PropertyUtils.getProperties("server.properties"));
		engine.setServerConfig(serverConfig);
		//ServerSocket socket = engine.createSecureServerSocket(8080);
		//socket.close();

		DefaultSSLContextCreator sslContextCreator = new DefaultSSLContextCreator(serverConfig);
		engine.setSslContextCreator(sslContextCreator);

		//socket = engine.createSecureServerSocket(8080);
		//socket.close();
	}

	@Test
	public void testSetHttpResponseInterceptor() {
		HttpResponseInterceptor interceptor = new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context)
					throws HttpException, IOException {
			}
		};
		engine.setHttpInterceptor(interceptor);
	}

	@Test
	public void testRegisterMXServer() {
		//engine.registerMXServer();
		//engine.unregisterMXServer();
	}

	@Test
	public void testReload() {
		//engine.reload();
	}

	@Test
	public void testGetMaxServerThreads() {
		ServerConfig serverConfig = new ServerConfig(PropertyUtils.getProperties("server.properties"));
		engine.setServerConfig(serverConfig);
		engine.setMaxServerThreads(3);
		assertEquals(3, engine.getMaxServerThreads());
	}

	@Test
	public void testGetPropertiesName() {
		engine.setPropertiesName("server.properties");
		assertEquals("server.properties", engine.getPropertiesName());
	}

	@Test
	public void testGetServerConfig() {
		ServerConfig serverConfig = new ServerConfig(PropertyUtils.getProperties("server.properties"));
		engine.setServerConfig(serverConfig);
		assertSame(serverConfig, engine.getServerConfig());
	}

	@Test
	public void testGetClassLoader() {
		assertNotNull(engine.getClassLoader());

		ClassLoader loader = getClass().getClassLoader();
		engine.setClassLoader(loader);
		assertSame(loader, engine.getClassLoader());
	}

}

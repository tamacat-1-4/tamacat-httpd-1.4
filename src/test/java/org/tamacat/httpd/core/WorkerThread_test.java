package org.tamacat.httpd.core;


import java.net.ServerSocket;
import java.util.Properties;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.jmx.BasicCounter;
import org.tamacat.httpd.handler.DefaultHttpService;
import org.tamacat.util.IOUtils;
import org.tamacat.util.PropertyUtils;

public class WorkerThread_test {

	DefaultWorker thread;

	public void testWorkerThread() throws Exception {
		Properties props = PropertyUtils.getProperties("server.properties");
		ServerConfig serverConfig = new ServerConfig(props);
//		HttpParamsBuilder paramsBuilder = new HttpParamsBuilder();
//		paramsBuilder.socketTimeout(serverConfig.getSocketTimeout())
//			  .socketBufferSize(serverConfig.getSocketBufferSize())
//			  .originServer(serverConfig.getParam("ServerName"));

		HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();

		//default interceptors
		procBuilder.addInterceptor(new ResponseDate());
		procBuilder.addInterceptor(new ResponseServer());
		procBuilder.addInterceptor(new ResponseContent());
		procBuilder.addInterceptor(new ResponseConnControl());

		DefaultHttpService service = new DefaultHttpService(
				procBuilder, new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory(), null, null
		);
		//DefaultHttpService service = new DefaultHttpService(
		//		procBuilder, new DefaultConnectionReuseStrategy(),
		//   	new DefaultHttpResponseFactory(), null, null,
		//    	paramsBuilder.buildParams());

		BasicCounter counter = new BasicCounter();
		ServerSocket serversocket = new ServerSocket(8080);
		thread = new DefaultWorker();
		thread.setHttpService(service);
		thread.setServerConfig(serverConfig);
		thread.setPerformanceCounter(counter);
		thread.setSocket(serversocket.accept());
		new Thread(thread).start();
		thread.isClosed();
		IOUtils.close(serversocket);
	}

}

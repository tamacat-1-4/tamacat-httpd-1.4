/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DefaultWorkerExecutorTest {

	@Test
	public void testGetExecutorService() {
		DefaultWorkerExecutor factory = new DefaultWorkerExecutor();
		ExecutorService executor = factory.getExecutorService();
		assertNotNull(executor);
//		executor.execute(new Thread(){
//			public void run() {
//				//System.out.println("run");
//			}
//		});
		for (int i=0; i<10; i++) {
			Future<Long> future = executor.submit(new CallbackImpl());
			try {
				future.get();
				//System.out.println("No." + i + "=" + future.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}

	@Test
	public void testGetExecutorServiceZero() {
		DefaultWorkerExecutor factory = new DefaultWorkerExecutor();
		ExecutorService executor = factory.getExecutorService();
		assertNotNull(executor);

		for (int i=0; i<10; i++) {
			Future<Long> future = executor.submit(new CallbackImpl());
			try {
				future.get();
				//System.out.println("No." + i + "=" + future.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}

	static class CallbackImpl implements Callable<Long> {

		private long time;
		public CallbackImpl() {
			this.time = System.currentTimeMillis();
		}

		@Override
		public Long call() throws Exception {
			return System.currentTimeMillis() - time;
		}
	}
}

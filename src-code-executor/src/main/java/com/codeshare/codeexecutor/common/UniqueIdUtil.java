/**
 * 
 */
package com.codeshare.codeexecutor.common;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeshare.codeexecutor.executor.CodeExecuteService;

/**
 * @author vibhor
 * 
 */
public class UniqueIdUtil {

	private static Logger LOGGER = LoggerFactory
			.getLogger(CodeExecuteService.class);

	private static final int MAX_NUMBER = 9999999;

	private static AtomicInteger NUMBER = new AtomicInteger(0);

	private static final String S = "S";

	public static synchronized String getUniqueId() {
		final String uniqueId = S + NUMBER.incrementAndGet();
		if (NUMBER.get() >= MAX_NUMBER) {
			LOGGER.info(
					"number has reached to its max value: {}, reinitializing from 1",
					NUMBER.get());
			NUMBER.set(0);
		}
		return uniqueId;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 40; i++) {
			new Thread(new Runnable() {

				public void run() {
					System.out.println(getUniqueId());
					System.out.println(getUniqueId());
					System.out.println(getUniqueId());
					System.out.println(getUniqueId());
				}
			}).start();
		}
	}
}

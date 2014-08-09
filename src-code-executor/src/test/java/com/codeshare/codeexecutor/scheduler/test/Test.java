/**
 * Aug 9, 2014
 * Test.java
 * 12:43:11 PM
 * vibhor
 */
package com.codeshare.codeexecutor.scheduler.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author vibhor
 * 
 */
public class Test {

	public static void main(String[] args) throws InterruptedException {

		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"src-code-executor-test-ctx.xml");

		Thread.sleep(50000);
		
		ctx.close();
	}
}

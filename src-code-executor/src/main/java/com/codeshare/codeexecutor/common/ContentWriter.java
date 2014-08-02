/**
 * 
 */
package com.codeshare.codeexecutor.common;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vibhor
 * 
 */
public class ContentWriter {

	private static Logger LOGGER = LoggerFactory.getLogger(ContentWriter.class);

	public static void write(final File outputFile, final String content) {
		try {
			FileWriter writer = new FileWriter(outputFile);
			LOGGER.debug("writing content: {} , to file: {}", content,
					outputFile.getAbsolutePath());
			writer.write(content);
			writer.flush();
			writer.close();
			writer = null;
			Thread.sleep(200);
			System.gc();
		} catch (final Exception e) {
			throw new IllegalStateException(
					"unable to write content to file. content: " + content
							+ " , file: " + outputFile.getAbsolutePath());
		}
	}

	public static void main(String[] args) {
		String content = "hello main";
		String fileName = "Hello";
		LOGGER.info("writing content: {} , to file: {}", content, fileName);
	}
}

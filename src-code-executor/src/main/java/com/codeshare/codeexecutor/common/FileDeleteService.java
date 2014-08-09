/**
 * 
 */
package com.codeshare.codeexecutor.common;

import java.io.File;

import org.apache.commons.io.FileDeleteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vibhor
 * 
 */
public class FileDeleteService {

	private static Logger LOGGER = LoggerFactory
			.getLogger(FileDeleteService.class);

	/**
	 * 
	 */
	public void delete(final File file) {
		try {
			if (file.canWrite()) {
				FileDeleteStrategy.FORCE.deleteQuietly(file);
			}
		} catch (Exception e) {
			LOGGER.error("unable to delete folder: {}", file.getAbsolutePath(),
					e);
		}
	}

}

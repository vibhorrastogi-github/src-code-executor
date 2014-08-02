/**
 * 
 */
package com.codeshare.codeexecutor.common;

import java.io.File;

import org.apache.commons.io.FileDeleteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.codeshare.codeexecutor.commandbox.CommandBoxContainer;

/**
 * @author vibhor
 * 
 */
public class DeleteGenFileExecutorService {

	private static Logger LOGGER = LoggerFactory
			.getLogger(DeleteGenFileExecutorService.class);

	@Autowired
	@Qualifier("commandBoxContainer")
	private CommandBoxContainer commandBoxContainer;

	public void deleteGeneratedDir(final String location, final String dir) {

		final String dirWithLocation = location + "/" + dir;

		LOGGER.debug("deleting dir: {}", dirWithLocation);

		final File dirWithLocationFile = new File(dirWithLocation);

		deleteFile(dirWithLocationFile);
	}

	/**
	 * 
	 */
	public void deleteFile(final File file) {
		try {
			FileDeleteStrategy.FORCE.deleteQuietly(file);
			// FileUtils.deleteDirectory(file);
		} catch (Exception e) {
			LOGGER.error("unable to delete folder: {}", file.getAbsolutePath(),
					e);
		}
	}

	public static void main(String[] args) {
		final String location = "C:/Users/vibhor/workspaces/rnd/cs";
		new DeleteGenFileExecutorService().deleteGeneratedDir(location, "S1");
	}

	static void printCurrentDir() {
		System.out.println(System.getProperty("user.dir"));

		File currentDirFile = new File("");
		String currentDir = currentDirFile.getAbsolutePath();
		System.out.println(currentDir);
	}
}

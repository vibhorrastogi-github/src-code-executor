/**
 * 
 */
package com.codeshare.codeexecutor.common;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	private final ExecutorService executorService;

	public DeleteGenFileExecutorService() {
		executorService = Executors.newFixedThreadPool(5);
	}

	public void deleteGeneratedFiles(final String location,
			final String fileNameWithoutExtension, final String language) {
		final FileCleanerJob fileCleanerJob = new FileCleanerJob(location,
				fileNameWithoutExtension, language);
		executorService.execute(fileCleanerJob);
	}

	private class FileCleanerJob implements Runnable {

		private final String location;

		private final String fileNameWithoutExtension;

		private final String language;

		public FileCleanerJob(final String location,
				final String fileNameWithoutExtension, final String language) {
			this.location = location;
			this.fileNameWithoutExtension = fileNameWithoutExtension;
			this.language = language;
		}

		public void run() {
			String fileNameWithLocation = location + "/"
					+ fileNameWithoutExtension;
			LOGGER.debug("deleting file: {}", fileNameWithLocation);
			final File dir = new File(fileNameWithLocation);
			if (dir.exists() && dir.isDirectory()) {
				deleteFolder(dir);
				return;
			}
			final Set<String> fileExtentionSet = commandBoxContainer
					.getGeneratedFileExtentionSet(language);
			for (final String fileExtention : fileExtentionSet) {
				final String fileAbsolutePath = fileNameWithLocation
						+ fileExtention;
				final File file = new File(fileAbsolutePath);
				if (file.exists() && file.isFile()) {
					file.delete();
				}
			}
		}
	}

	public void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public static void main(String[] args) {
		final String location = "C:/Users/vibhor/workspaces/rnd/cs";
		new DeleteGenFileExecutorService().deleteGeneratedFiles(location, "S1",
				"java");
	}

	static void printCurrentDir() {
		System.out.println(System.getProperty("user.dir"));

		File currentDirFile = new File("");
		String currentDir = currentDirFile.getAbsolutePath();
		System.out.println(currentDir);
	}
}

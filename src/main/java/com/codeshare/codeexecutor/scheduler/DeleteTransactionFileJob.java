/**
 * Aug 9, 2014
 * DeleteTransactionFileJob.java
 * 12:40:35 PM
 * vibhor
 */
package com.codeshare.codeexecutor.scheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.codeshare.codeexecutor.common.FileDeleteService;

/**
 * @author vibhor
 * 
 */
public class DeleteTransactionFileJob {

	private static Logger LOGGER = LoggerFactory
			.getLogger(DeleteTransactionFileJob.class);

	@Value("${file.delete.eligibility.last.access.time.in.millis}")
	private long lastAccessTimeInMillisForDeletion;

	@Value("${src.code.executor.transaction.file.home}")
	private String txnFileHome;

	@Autowired
	private FileDeleteService fileDeleteService;

	@Scheduled(cron = "${delete.transaction.file.cron}")
	public void delete() {

		LOGGER.debug("***********file deletion service start @ {} ***********",
				new Date());

		try {
			deleteFiles();
		} catch (final Exception e) {
			LOGGER.error("unable to delete files", e);
		}

		LOGGER.debug("***********file deletion service end @ {} ***********",
				new Date());
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void deleteFiles() throws IOException {

		final List<File> eligibleToDeleteFileList = getAllFilesEligibleToDelete();

		LOGGER.debug("eligibleToDeleteFileList.size(): {}",
				eligibleToDeleteFileList.size());

		if (eligibleToDeleteFileList != null) {

			for (final File file : eligibleToDeleteFileList) {

				fileDeleteService.delete(file);
			}
		}
	}

	/**
	 * @throws IOException
	 * 
	 */
	private List<File> getAllFilesEligibleToDelete() throws IOException {

		List<File> eligibleToDeleteFileList = null;

		final File dir = new File(txnFileHome);

		if (dir.exists() && dir.isDirectory()) {

			eligibleToDeleteFileList = new ArrayList<File>();

			final File[] files = dir.listFiles();

			final long currentTime = new Date().getTime();

			for (final File file : files) {

				if (isFileEligibleToDelete(file, currentTime)) {

					eligibleToDeleteFileList.add(file);
				}
			}
		}

		return eligibleToDeleteFileList;
	}

	/**
	 * @throws IOException
	 * 
	 */
	private boolean isFileEligibleToDelete(final File file,
			final long currentTime) throws IOException {

		final Path source = Paths.get(file.getAbsolutePath());

		final BasicFileAttributes basicFileAttributes = Files.readAttributes(
				source, BasicFileAttributes.class);

		final FileTime fileTime = basicFileAttributes.lastAccessTime();

		final long lasAccessTime = fileTime.toMillis();

		if ((currentTime - lastAccessTimeInMillisForDeletion) > lasAccessTime) {

			return true;
		}
		return false;
	}
}

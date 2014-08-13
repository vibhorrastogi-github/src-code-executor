/**
 * 
 */
package com.codeshare.codeexecutor.executor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.codeshare.codeexecutor.commandbox.CommandBoxContainer;
import com.codeshare.codeexecutor.commandbox.Language;
import com.codeshare.codeexecutor.common.CommandType;
import com.codeshare.codeexecutor.common.ContentWriter;
import com.codeshare.codeexecutor.common.FileDeleteService;
import com.codeshare.codeexecutor.common.bean.CodeExecuteRequest;
import com.codeshare.codeexecutor.common.bean.CodeExecuteResponse;
import com.codeshare.codeexecutor.common.bean.CommandInfo;

/**
 * @author vibhor
 * 
 */
public abstract class CodeExecuteService implements InitializingBean {

	private static Logger LOGGER = LoggerFactory
			.getLogger(CodeExecuteService.class);

	protected static final String DEFAULT_FILE_NAME = "MySrcCode";

	@Value("${execution.process.timeout.in.millis}")
	private int processTimeoutInMillis;

	@Autowired
	@Qualifier("commandBoxContainer")
	protected CommandBoxContainer commandBoxContainer;

	@Autowired
	@Qualifier("cleanerExecutorService")
	protected FileDeleteService fileDeleteService;

	@Value("${src.code.executor.transaction.file.home}")
	protected String txnFileHome;

	@Value("${output.stream.max.char.size}")
	private int outputStreamMaxCharSize;

	protected void execute(final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("executing compiled code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());
		try {
			final CommandInfo commandInfo = commandBoxContainer.getComandInfo(
					codeExecuteRequest.getLang(), CommandType.EXECUTE);

			executeCmd(commandInfo.getCmd(), codeExecuteRequest, codeExecuteResponse, true);

		} catch (final Exception e) {
			throw new IllegalStateException(
					"unable to execute compiled code for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	protected boolean validateCompilation(
			final CodeExecuteResponse codeExecuteResponse,
			final CodeExecuteRequest codeExecuteRequest) {

		LOGGER.debug(
				"validating compilation of source code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		boolean compilationSuccess = true;

		if (StringUtils.hasText(codeExecuteResponse.getStdout())) {

			final String output = codeExecuteResponse.getStdout().toLowerCase();

			if (output.trim().length() != 0
					&& (output.contains("error") || output.contains("fatal")
							|| output.contains("exception")
							|| output.contains("Error")
							|| output.contains("Exception") || output
								.contains("Fatal"))) {

				LOGGER.debug(
						"compilation failed for codeExecuteRequest id: {} , with output: {}",
						codeExecuteRequest.getId(), output);

				compilationSuccess = false;
			}
		}
		return compilationSuccess;
	}

	protected void compile(final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("compiling source code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());
		try {

			final CommandInfo commandInfo = commandBoxContainer.getComandInfo(
					codeExecuteRequest.getLang(), CommandType.COMPILE);

			executeCmd(commandInfo.getCmd(), codeExecuteRequest, codeExecuteResponse, false);

		} catch (final Exception e) {
			throw new IllegalStateException(
					"unable to compile code for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	private void executeCmd(final String command,
			final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final boolean isExecute) {

		LOGGER.debug("executing command: {} , for codeExecuteRequest id: {}",
				command, codeExecuteRequest.getId());

		try {

			final DefaultExecutor executor = new DefaultExecutor();

			executor.setWorkingDirectory(new File(txnFileHome
					+ codeExecuteRequest.getId()));

			final ExecuteWatchdog watchdog = new ExecuteWatchdog(
					processTimeoutInMillis);

			executor.setWatchdog(watchdog);

			final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

			final MyPumpStreamHandler streamHandler = new MyPumpStreamHandler(
					(isExecute ? codeExecuteRequest.getStdin() : null),
					watchdog, outputStreamMaxCharSize);

			executor.setStreamHandler(streamHandler.getPumpStreamHandler());

			final long startTime = System.currentTimeMillis();

			executor.execute(CommandLine.parse(command), resultHandler);

			resultHandler.waitFor();

			final long executionTime = System.currentTimeMillis() - startTime;

			setStdout(streamHandler, resultHandler, codeExecuteResponse,
					executionTime);

		} catch (final Exception e) {
			throw new IllegalStateException("unable to execute command: "
					+ command + " for codeExecuteRequest id: "
					+ codeExecuteRequest.getId(), e);
		}
	}

	/**
	 * @param streamHandler
	 * @param resultHandler
	 * @param codeExecuteResponse
	 * @param executionTime
	 */
	private void setStdout(final MyPumpStreamHandler streamHandler,
			final DefaultExecuteResultHandler resultHandler,
			final CodeExecuteResponse codeExecuteResponse,
			final long executionTime) {

		LOGGER.error("", resultHandler.getException());

		codeExecuteResponse.setStdout(streamHandler.getStdout());

		if (streamHandler.isOutputStreamOverflowed()) {

			codeExecuteResponse.setStdout(codeExecuteResponse.getStdout()
					+ "\n output stream overflowed.".trim());
		}

		codeExecuteResponse.setHandle(resultHandler.getExitValue());

		if (executionTime >= processTimeoutInMillis) {

			codeExecuteResponse.setStdout(codeExecuteResponse.getStdout()
					+ "\n process timed out.".trim());
		}

		codeExecuteResponse.setExecutionTime(executionTime);
	}

	protected boolean createSourceCodeFile(
			final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("creating source code file for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		boolean isFileSuccessfullyCreated = false;

		final String srcCode = codeExecuteRequest.getSrcCode();

		if (StringUtils.hasText(language.getPatternInSrcCode())) {

			String pattern = language.getPatternInSrcCode();

			if (!srcCode.contains(pattern)) {
				codeExecuteResponse.setStdout("invalid src code, " + pattern
						+ " must exist");
				return isFileSuccessfullyCreated;
			}
		}
		try {
			final File dir = new File(txnFileHome + codeExecuteRequest.getId());

			if (dir.exists()) {
				fileDeleteService.delete(dir);

			}
			dir.mkdir();

			final File srcCodeFile = new File(dir.getAbsolutePath() + "/"
					+ DEFAULT_FILE_NAME + "." + language.getName());

			if (srcCodeFile.exists()) {
				fileDeleteService.delete(srcCodeFile);
			}
			srcCodeFile.createNewFile();

			ContentWriter.write(srcCodeFile, srcCode);

			isFileSuccessfullyCreated = true;
			return isFileSuccessfullyCreated;
		} catch (final IOException e) {
			throw new IllegalStateException(
					"unable to create source code file for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}
}

/**
 * 
 */
package com.codeshare.codeexecutor.executor;

import java.io.File;

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

import com.codeshare.codeexecutor.commandbox.CommandBoxContainer;
import com.codeshare.codeexecutor.commandbox.Language;
import com.codeshare.codeexecutor.common.CommandType;
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

	@Value("${execution.process.timeout.in.millis}")
	private int processTimeoutInMillis;

	@Autowired
	@Qualifier("commandBoxContainer")
	protected CommandBoxContainer commandBoxContainer;

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

			executeCmd(commandInfo.getCmd(), codeExecuteRequest,
					codeExecuteResponse, true);

			codeExecuteResponse.setExecuted(true);

		} catch (final Exception e) {
			throw new IllegalStateException(
					"unable to execute compiled code for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	protected void compile(final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("compiling source code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());
		try {

			final CommandInfo commandInfo = commandBoxContainer.getComandInfo(
					codeExecuteRequest.getLang(), CommandType.COMPILE);

			executeCmd(commandInfo.getCmd(), codeExecuteRequest,
					codeExecuteResponse, false);

		} catch (final Exception e) {
			throw new IllegalStateException(
					"unable to compile code for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	private void executeCmd(String command,
			final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final boolean isExecute) {

		try {

			final String workingDir = txnFileHome + codeExecuteRequest.getId();

			command = updateCommand(command, workingDir);

			LOGGER.debug(
					"executing command: {} , for codeExecuteRequest id: {}",
					command, codeExecuteRequest.getId());

			final DefaultExecutor executor = new DefaultExecutor();

			executor.setWorkingDirectory(new File(workingDir));

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
	 * @param command
	 * @param workingDir
	 * @return
	 */
	private String updateCommand(String command, final String workingDir) {

		command = command.replaceAll("#PATH", workingDir.replace("\\", "/"));

		return command;
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

		LOGGER.debug("", resultHandler.getException());

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

}

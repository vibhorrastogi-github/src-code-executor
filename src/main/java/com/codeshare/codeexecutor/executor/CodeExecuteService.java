/**
 * 
 */
package com.codeshare.codeexecutor.executor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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

	// protected String txnFileHome = System.getProperty("user.home")
	// + "/msc/txFiles/";

	protected void execute(final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("executing compiled code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		try {
			final long startTime = System.currentTimeMillis();

			final CommandInfo commandInfo = commandBoxContainer.getComandInfo(
					codeExecuteRequest.getLang(), CommandType.EXECUTE);

			final String command = createCommand(commandInfo,
					codeExecuteRequest, language);

			final Process process = executeCmd(command, codeExecuteRequest);

			if (StringUtils.hasText(codeExecuteRequest.getStdin())) {
				writeStdInput(process, command, codeExecuteRequest);
			}

			final boolean isProcessTimedOut = waitForProcessCompetition(
					process, command, codeExecuteRequest);

			if (isProcessTimedOut) {
				codeExecuteResponse.setStdout("Process timed  out after "
						+ processTimeoutInMillis + " ms during execution");
				return;
			}

			final String procesOutput = readFromProcess(process, command,
					codeExecuteRequest);

			codeExecuteResponse.setStdout(procesOutput.toString());

			final long end_time = System.currentTimeMillis();

			final long execution_time = end_time - startTime;

			codeExecuteResponse.setExecutionTime(execution_time);
		} catch (final Exception e) {
			throw new IllegalStateException(
					"unable to execute compiled code for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	private void writeStdInput(Process process, String command,
			CodeExecuteRequest codeExecuteRequest) throws IOException {

		LOGGER.debug(
				"writing std input to process for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				process.getOutputStream()));

		bw.write(codeExecuteRequest.getStdin());
		bw.newLine();
		bw.flush();
		bw.close();
		bw = null;
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

			final String command = createCommand(commandInfo,
					codeExecuteRequest, language);

			final Process process = executeCmd(command, codeExecuteRequest);

			final boolean isProcessTimedOut = waitForProcessCompetition(
					process, command, codeExecuteRequest);

			if (isProcessTimedOut) {
				codeExecuteResponse.setStdout("Process timed out after "
						+ processTimeoutInMillis + " ms during compilation");
				return;
			}

			final String procesOutput = readFromProcess(process, command,
					codeExecuteRequest);

			codeExecuteResponse.setStdout(procesOutput.toString());

		} catch (final Exception e) {
			throw new IllegalStateException(
					"unable to compile code for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	private String createCommand(final CommandInfo commandInfo,
			final CodeExecuteRequest codeExecuteRequest, final Language language) {

		String command = commandInfo.getCmd();

		return command;
	}

	private String readFromProcess(final Process process, final String command,
			final CodeExecuteRequest codeExecuteRequest) {

		LOGGER.debug(
				"reading from process for command: {} , for codeExecuteRequest id: {}",
				command, codeExecuteRequest.getId());

		final String output;

		BufferedReader br = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		final StringBuilder outputBuilder = new StringBuilder();

		String line = null;

		try {

			while ((line = br.readLine()) != null) {
				outputBuilder.append(line + "\n");
			}
			br.close();
			br = null;
			output = outputBuilder.toString();

			LOGGER.debug(
					"output: {} , for command: {} , for codeExecuteRequest id: {}",
					output, command, codeExecuteRequest.getId());

		} catch (final IOException e) {
			throw new IllegalStateException(
					"unable to read from process for command: " + command
							+ " for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
		return output;
	}

	private Process executeCmd(final String command,
			final CodeExecuteRequest codeExecuteRequest) {

		LOGGER.debug("executing command: {} , for codeExecuteRequest id: {}",
				command, codeExecuteRequest.getId());

		final ProcessBuilder pb = new ProcessBuilder();

		pb.redirectErrorStream(true);

		pb.directory(new File(txnFileHome + codeExecuteRequest.getId()));

		pb.command(new String[] { "cmd", "/c", command });

		// pb.command(new String[] { "bash / csh", "/c", command }); for linux

		try {
			final Process process = pb.start();

			LOGGER.info("pb.directory(): {}", pb.directory());
			LOGGER.info("pb.command(): {}", pb.directory());
			// LOGGER.info("pb.environment(): {}", pb.environment());
			pb.inheritIO();

			return process;
		} catch (final IOException e) {
			throw new IllegalStateException("unable to execute command: "
					+ command + " for codeExecuteRequest id: "
					+ codeExecuteRequest.getId(), e);
		}

	}

	private boolean waitForProcessCompetition(final Process process,
			final String command, final CodeExecuteRequest codeExecuteRequest) {

		boolean isProcessTimedOut = false;

		LOGGER.debug(
				"waiting for process to complete for command: {} , for codeExecuteRequest id: {}",
				command, codeExecuteRequest.getId());

		final long startTime = System.currentTimeMillis();
		final long endTime = startTime + processTimeoutInMillis;

		while (isProcessAlive(process)
				&& (System.currentTimeMillis() < endTime)) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				LOGGER.error("", e);
			}
		}
		if (isProcessAlive(process)) {
			isProcessTimedOut = true;
		}
		return isProcessTimedOut;
	}

	private boolean isProcessAlive(final Process process) {
		try {
			process.exitValue();
			return false;
		} catch (final IllegalThreadStateException e) {
			return true;
		}
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

	public static void main(String[] args) {
		String command = "c++ -o #FILE_NAME";

		if (command.contains("#FILE_NAME")) {

			command = command.replaceAll("#FILE_NAME", "hello");
		}
		System.out.println(command);
	}
}

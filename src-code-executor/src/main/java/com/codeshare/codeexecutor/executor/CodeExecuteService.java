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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import com.codeshare.codeexecutor.commandbox.CommandBoxContainer;
import com.codeshare.codeexecutor.commandbox.Language;
import com.codeshare.codeexecutor.common.DeleteGenFileExecutorService;
import com.codeshare.codeexecutor.common.CommandType;
import com.codeshare.codeexecutor.common.ContentWriter;
import com.codeshare.codeexecutor.common.bean.CodeExecuteRequest;
import com.codeshare.codeexecutor.common.bean.CodeExecuteResponse;
import com.codeshare.codeexecutor.common.bean.CommandInfo;

/**
 * @author vibhor
 * 
 */
public abstract class CodeExecuteService {

	private static Logger LOGGER = LoggerFactory
			.getLogger(CodeExecuteService.class);

	protected static final String DEFAULT_CLASS_NAME = "MySrcCode";

	private static final int PROCESS_TIMEOUT_IN_MILLIS = 5 * 1000;

	@Autowired
	@Qualifier("commandBoxContainer")
	protected CommandBoxContainer commandBoxContainer;

	@Autowired
	@Qualifier("cleanerExecutorService")
	protected DeleteGenFileExecutorService deleteGenFileExecutorService;

	protected void execute(final CodeExecuteRequest codeExecuteRequest,
			final String fileName,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("executing compiled code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		try {
			final long startTime = System.currentTimeMillis();

			final CommandInfo commandInfo = commandBoxContainer.getComandInfo(
					codeExecuteRequest.getLang(), CommandType.EXECUTE);

			final String command = createCommand(commandInfo, fileName,
					codeExecuteRequest, language);

			final Process process = executeCmd(command, codeExecuteRequest);

			if (StringUtils.hasText(codeExecuteRequest.getStdin())) {
				writeStdInput(process, command, codeExecuteRequest);
			}

			final boolean isProcessTimedOut = waitForProcessCompetition(
					process, command, codeExecuteRequest);

			if (isProcessTimedOut) {
				codeExecuteResponse.setStdout("Process timed out after "
						+ PROCESS_TIMEOUT_IN_MILLIS + " ms during execution");
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

		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				process.getOutputStream()));

		bw.write(codeExecuteRequest.getStdin());
		bw.newLine();
		bw.flush();
		bw.close();
	}

	/*
	 * protected void executeAndTest(final CodeExecuteRequest
	 * codeExecuteRequest, final String fileName, final CodeExecuteResponse
	 * codeExecuteResponse, final Language language, final Set<TestCase>
	 * testCases) {
	 * LOGGER.info("executing compiled code for codeExecuteRequest id: {}",
	 * codeExecuteRequest.getId()); try { final long start_time =
	 * System.currentTimeMillis(); final CommandInfo commandInfo =
	 * commandBoxContainer.getComandInfo( codeExecuteRequest.getLanguage(),
	 * CommandType.EXECUTE);
	 * 
	 * final String command = createCommand(commandInfo, fileName,
	 * codeExecuteRequest, language);
	 * 
	 * final Process process = executeCmd(command, codeExecuteRequest);
	 * 
	 * executeTestCases(process, testCases, command, codeExecuteRequest,
	 * codeExecuteResponse);
	 * 
	 * final boolean isProcessTimedOut = waitForProcessCompetition( process,
	 * command, codeExecuteRequest);
	 * 
	 * if (isProcessTimedOut) { codeExecuteResponse
	 * .setStdout("Process timed out after " + PROCESS_TIMEOUT_IN_MILLIS +
	 * " ms during compilation"); return; }
	 * 
	 * final long end_time = System.currentTimeMillis(); final long
	 * execution_time = end_time - start_time;
	 * codeExecuteResponse.setExecutionTime(execution_time); } catch (final
	 * Exception e) { throw new IllegalStateException(
	 * "unable to execute compiled code for codeExecuteRequest id: " +
	 * codeExecuteRequest.getId(), e); } }
	 */

	protected boolean validateCompilation(
			final CodeExecuteResponse codeExecuteResponse,
			final CodeExecuteRequest codeExecuteRequest) {

		LOGGER.debug(
				"validating compilation of source code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		boolean compilationSuccess = true;

		if (StringUtils.hasText(codeExecuteResponse.getStdout())) {

			final String output = codeExecuteResponse.getStdout().toLowerCase();

			if (output.contains("error") || output.contains("fatal")
					|| output.contains("exception")) {

				LOGGER.debug(
						"compilation failed for codeExecuteRequest id: {} , with output: {}",
						codeExecuteRequest.getId(), output);

				compilationSuccess = false;
			}
		}
		return compilationSuccess;
	}

	protected void compile(final CodeExecuteRequest codeExecuteRequest,
			final String fileName,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("compiling source code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());
		try {
			final long startTime = System.currentTimeMillis();

			final CommandInfo commandInfo = commandBoxContainer.getComandInfo(
					codeExecuteRequest.getLang(), CommandType.COMPILE);

			final String command = createCommand(commandInfo, fileName,
					codeExecuteRequest, language);

			final Process process = executeCmd(command, codeExecuteRequest);

			final boolean isProcessTimedOut = waitForProcessCompetition(
					process, command, codeExecuteRequest);

			if (isProcessTimedOut) {
				codeExecuteResponse.setStdout("Process timed out after "
						+ PROCESS_TIMEOUT_IN_MILLIS + " ms during compilation");
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
					"unable to compile code for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	private String createCommand(final CommandInfo commandInfo,
			final String fileName, final CodeExecuteRequest codeExecuteRequest,
			final Language language) {

		String command = commandInfo.getCmd();

		final String argument = commandInfo.getArg();

		if (language.isCreateFolder()) {

			command = command.replaceAll("#FOLDER_NAME",
					codeExecuteRequest.getId());
		}

		if (command.contains("#FILE_NAME")) {

			command = command.replaceAll("#FILE_NAME",
					codeExecuteRequest.getId());
		}

		final String fullCommand = command + fileName + argument;

		return fullCommand;
	}

	private String readFromProcess(final Process process, final String command,
			final CodeExecuteRequest codeExecuteRequest) {

		LOGGER.debug(
				"reading from process for command: {} , for codeExecuteRequest id: {}",
				command, codeExecuteRequest.getId());

		final String output;

		final BufferedReader br = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		final StringBuilder outputBuilder = new StringBuilder();

		String line = null;

		try {

			while ((line = br.readLine()) != null) {
				outputBuilder.append(line + "\n");
			}
			br.close();

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

	/*
	 * private void executeTestCases(final Process process, final Set<TestCase>
	 * testCases, final String command, final CodeExecuteRequest
	 * codeExecuteRequest, final CodeExecuteResponse codeExecuteResponse) throws
	 * IOException {
	 * LOGGER.info("executing test cases for codeExecuteRequest id: {}",
	 * codeExecuteRequest.getId()); final BufferedWriter bw = new
	 * BufferedWriter(new OutputStreamWriter( process.getStdoutStream()));
	 * 
	 * final Set<TestCaseResponse> testCaseResponses = new
	 * HashSet<TestCaseResponse>(); bw.write(testCases.size() + "");
	 * bw.newLine(); for (final TestCase testCase : testCases) { final long
	 * start_time = System.currentTimeMillis(); bw.write(testCase.getInput());
	 * bw.newLine(); } bw.flush(); readFromProcess(process, command,
	 * codeExecuteRequest, codeExecuteResponse); final long end_time =
	 * System.currentTimeMillis(); // final long execution_time = end_time -
	 * start_time; final TestCaseResponse testCaseResponse = new
	 * TestCaseResponse(); testCaseResponses.add(testCaseResponse); //
	 * testCaseResponse.setDesiredOutput(testCase.getStdout()); //
	 * testCaseResponse.setExecutionTimeInMillis(execution_time); //
	 * testCaseResponse.setInput(testCase.getInput());
	 * testCaseResponse.setStdout(codeExecuteResponse.getStdout()); bw.close();
	 * }
	 */

	private Process executeCmd(final String command,
			final CodeExecuteRequest codeExecuteRequest) {

		LOGGER.debug("executing command: {} , for codeExecuteRequest id: {}",
				command, codeExecuteRequest.getId());

		final ProcessBuilder pb = new ProcessBuilder();

		pb.redirectErrorStream(true);

		pb.command(new String[] { "cmd", "/c", command });
		try {
			final Process process = pb.start();
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
		final long endTime = startTime + PROCESS_TIMEOUT_IN_MILLIS;

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
			final String fileNameWithExtension, final Language language) {

		LOGGER.info("creating source code file for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		boolean isFileSuccessfullyCreated = false;
		String folderName = ".";

		final String srcCode = codeExecuteRequest.getSrcCode();
		try {
			if (language.isCreateFolder()) {
				if (!srcCode.contains("public class " + DEFAULT_CLASS_NAME)) {
					codeExecuteResponse
							.setStdout("invalid src code, public class "
									+ DEFAULT_CLASS_NAME
									+ " must exist for language: "
									+ codeExecuteRequest.getLang());
					return isFileSuccessfullyCreated;
				}

				folderName = codeExecuteRequest.getId();

				final File folder = new File(folderName);

				if (folder.exists()) {
					folder.delete();
				}
				folder.mkdir();
			}
			final File srcCodeFile = new File(folderName + "/"
					+ fileNameWithExtension);

			if (srcCodeFile.exists()) {
				srcCodeFile.delete();
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

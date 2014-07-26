/**
 * 
 */
package com.codeshare.codeexecutor.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeshare.codeexecutor.commandbox.Language;
import com.codeshare.codeexecutor.common.bean.CodeExecuteRequest;
import com.codeshare.codeexecutor.common.bean.CodeExecuteResponse;

/**
 * @author vibhor
 * 
 */
public class CodeExecuteServiceMgr extends CodeExecuteService {

	private static Logger LOGGER = LoggerFactory
			.getLogger(CodeExecuteServiceMgr.class);

	private static final char _DOT = '.';

	private static final String USER_DIR_LOC = System.getProperty("user.dir");

	public CodeExecuteResponse compileAndExecute(
			final CodeExecuteRequest codeExecuteRequest) throws Exception {

		final Language language = commandBoxContainer
				.getLanguage(codeExecuteRequest.getLang());

		String fileNameWithoutExtension = null;
		String fileNameWithExtension = null;

		if (language.isCreateFolder()) {
			fileNameWithoutExtension = DEFAULT_CLASS_NAME;
			fileNameWithExtension = DEFAULT_CLASS_NAME + _DOT
					+ language.getName();
		} else {
			fileNameWithoutExtension = codeExecuteRequest.getId();
			fileNameWithExtension = codeExecuteRequest.getId() + _DOT
					+ language.getName();
		}

		final CodeExecuteResponse codeExecuteResponse = new CodeExecuteResponse(
				codeExecuteRequest.getId());

		codeExecuteResponse.setLanguage(language.getName());
		codeExecuteResponse.setStdin(codeExecuteRequest.getStdin());

		try {
			final boolean isFileSuccessfullyCreated = createSourceCodeFile(
					codeExecuteRequest, codeExecuteResponse,
					fileNameWithExtension, language);

			if (isFileSuccessfullyCreated) {

				compile(codeExecuteRequest, fileNameWithExtension,
						codeExecuteResponse, language);

				/**
				 * validate whether file is created or not?
				 */
				// TODO
				final boolean compilationSuccess = validateCompilation(
						codeExecuteResponse, codeExecuteRequest);

				if (compilationSuccess) {

					LOGGER.debug(
							"compilation succeeded for codeExecuteRequest id: {} , with output: {}",
							codeExecuteRequest.getId(),
							codeExecuteResponse.getStdout());

					execute(codeExecuteRequest, fileNameWithoutExtension,
							codeExecuteResponse, language);
				}
			}
			return codeExecuteResponse;
		} catch (final Exception e) {
			LOGGER.error(
					"unable to fulfill code execution request for codeExecuteRequest id: {}",
					codeExecuteRequest.getId(), e);
			throw e;
		} finally {
			deleteGenFileExecutorService.deleteGeneratedFiles(USER_DIR_LOC,
					(language.isCreateFolder() ? codeExecuteRequest.getId()
							: fileNameWithoutExtension), codeExecuteRequest
							.getLang());
		}
	}

	public CodeExecuteResponse compileAndExecuteSourceCodeWithTestCases() {
		return null;
	}
}

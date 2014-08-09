/**
 * 
 */
package com.codeshare.codeexecutor.executor;

import java.io.File;

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

	public CodeExecuteResponse compileAndExecute(
			final CodeExecuteRequest codeExecuteRequest) throws Exception {

		final Language language = commandBoxContainer
				.getLanguage(codeExecuteRequest.getLang());

		final CodeExecuteResponse codeExecuteResponse = new CodeExecuteResponse(
				codeExecuteRequest.getId());

		codeExecuteResponse.setLanguage(language.getName());
		codeExecuteResponse.setStdin(codeExecuteRequest.getStdin());

		try {
			final boolean isFileSuccessfullyCreated = createSourceCodeFile(
					codeExecuteRequest, codeExecuteResponse, language);

			if (isFileSuccessfullyCreated) {

				compile(codeExecuteRequest, codeExecuteResponse, language);

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

					execute(codeExecuteRequest, codeExecuteResponse, language);
				}
			}
			return codeExecuteResponse;
		} catch (final Exception e) {
			LOGGER.error(
					"unable to fulfill code execution request for codeExecuteRequest id: {}",
					codeExecuteRequest.getId(), e);
			throw e;
		}
		// finally {
		// deleteGenFileExecutorService.deleteGeneratedDir(txnFileHome,
		// codeExecuteRequest.getId());
		//
		// }
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {

		LOGGER.info("txnFileHome: {}", txnFileHome);

		final File mscDir = new File(txnFileHome);

		if (mscDir.exists() && mscDir.isDirectory()) {

		} else {
			mscDir.mkdirs();
		}
	}
}
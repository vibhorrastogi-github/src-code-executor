/**
 * 
 */
package com.codeshare.codeexecutor.executor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.codeshare.codeexecutor.commandbox.Language;
import com.codeshare.codeexecutor.common.ContentWriter;
import com.codeshare.codeexecutor.common.FileDeleteService;
import com.codeshare.codeexecutor.common.bean.CodeExecuteRequest;
import com.codeshare.codeexecutor.common.bean.CodeExecuteResponse;

/**
 * @author vibhor
 * 
 */
public class CodeExecuteHelper implements InitializingBean {

	private static Logger LOGGER = LoggerFactory
			.getLogger(CodeExecuteHelper.class);

	protected static final String DEFAULT_FILE_NAME = "MySrcCode";

	@Autowired
	@Qualifier("cleanerExecutorService")
	protected FileDeleteService fileDeleteService;

	@Value("${src.code.executor.transaction.file.home}")
	protected String txnFileHome;

	@Value("${compilation.failure.tokens}")
	private String compilationFailureTokens;

	private Set<String> compilationFailureTokenSet;

	protected void validateCompilation(
			final CodeExecuteResponse codeExecuteResponse,
			final CodeExecuteRequest codeExecuteRequest) {

		LOGGER.debug(
				"validating compilation of source code for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		if (StringUtils.hasText(codeExecuteResponse.getStdout())) {

			final String output = codeExecuteResponse.getStdout().trim()
					.toLowerCase();

			if (output.length() > 0) {

				for (final String token : compilationFailureTokenSet) {

					if (output.contains(token)) {

						LOGGER.debug(
								"compilation failed for codeExecuteRequest id: {} , with output: {}",
								codeExecuteRequest.getId(), output);
						return;
					}
				}
			}
		}
		codeExecuteResponse.setCompiled(true);
	}

	protected void createSourceCodeFile(
			final CodeExecuteRequest codeExecuteRequest,
			final CodeExecuteResponse codeExecuteResponse,
			final Language language) {

		LOGGER.info("creating source code file for codeExecuteRequest id: {}",
				codeExecuteRequest.getId());

		final String srcCode = codeExecuteRequest.getSrcCode();

		if (StringUtils.hasText(language.getPatternInSrcCode())) {

			String pattern = language.getPatternInSrcCode();

			if (!srcCode.contains(pattern)) {
				codeExecuteResponse.setStdout("invalid src code, " + pattern
						+ " must exist");
				return;
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

			codeExecuteResponse.setFileCreated(true);
		} catch (final IOException e) {
			throw new IllegalStateException(
					"unable to create source code file for codeExecuteRequest id: "
							+ codeExecuteRequest.getId(), e);
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {

		compilationFailureTokenSet = new HashSet<String>();

		for (final String token : compilationFailureTokens.split(",")) {

			compilationFailureTokenSet.add(token.toLowerCase());
		}
	}
}

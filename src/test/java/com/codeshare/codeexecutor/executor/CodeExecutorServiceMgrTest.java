/**
 * Jul 27, 2014
 * CodeExecutorServiceMgrTest.java
 * 6:04:02 PM
 * vibhor
 */
package com.codeshare.codeexecutor.executor;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.codeshare.codeexecutor.common.bean.CodeExecuteRequest;
import com.codeshare.codeexecutor.common.bean.CodeExecuteResponse;

/**
 * @author vibhor
 * 
 */
public class CodeExecutorServiceMgrTest {

	private static final String JAVA_SOURCE_CODE_NON_INPUT = "public class MySrcCode { public static void main(String[] args) { System.out.println(\"Java-#REP\"); new AnotherClass(); } } class AnotherClass {	public AnotherClass() {	System.out.println(\"Code from another new class in same file\"); } }";

	private static final String JAVA_SOURCE_CODE_WITH_INPUT = "import java.util.Scanner; public class Solution { public static void main(String[] args) { final Scanner in = new Scanner(System.in); final int n = in.nextInt(); in.nextLine(); for (int i = 0; i < n; i++) { String nbrStr = in.nextLine(); String[] nbrStrArr = nbrStr.split(\" \"); int[] nbrArr = new int[nbrStrArr.length]; int j = 0; for (String s : nbrStrArr) { nbrArr[j] = Integer.valueOf(s).intValue(); j++; }	int maxNbr = getMax(nbrArr); System.out.println(maxNbr); } in.close(); } private static int getMax(final int[] nbrArr) { int maxNbr = nbrArr[0]; for (int i : nbrArr) { if (i > maxNbr) { maxNbr = i; } } return maxNbr;} }";

	private static final String C_SOURCE_CODE_NON_INPUT = "\n#include<stdio.h> \n#include<conio.h>\nint main() { \nint i=1/1; \nprintf(\"C-#REP\"); \nreturn 0; \n}";

	// static String s =
	// "#include <stdio.h>\n\nint main(void) {\nprintf(\"hello\");\nreturn 0;\n}";

	public static void main(String[] args) throws Exception {

		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"src-code-executor-test-ctx.xml");

		final CodeExecuteServiceMgr codeExecuteServiceMgr = ctx
				.getBean(CodeExecuteServiceMgr.class);

		loadTest(codeExecuteServiceMgr);

		Thread.sleep(Long.MAX_VALUE);
		ctx.close();
	}

	private static void loadTest(
			final CodeExecuteServiceMgr codeExecuteServiceMgr) {

		final AtomicInteger count = new AtomicInteger(0);
		for (int i = 0; i < 1; i++) {

			new Thread(new Runnable() {

				public void run() {

					try {

						CodeExecuteRequest request = getCNonInputRequest();

						request.setId("S1c" + count.incrementAndGet());

						request.setSrcCode(request.getSrcCode().replaceAll(
								"#REP", request.getId()));

//						CodeExecuteResponse response = codeExecuteServiceMgr
//								.compileAndExecute(request);
//
//						System.out.println(request.getId() + "="
//								+ response.getStdout());

						request = getJavaNonInputRequest();

						request.setId("S1j" + count.incrementAndGet());

						request.setSrcCode(request.getSrcCode().replaceAll(
								"#REP", request.getId()));

						CodeExecuteResponse response = codeExecuteServiceMgr
								.compileAndExecute(request);

						System.out.println(request.getId() + "="
								+ response.getStdout());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	/**
	 * 
	 */
	private static CodeExecuteRequest getCNonInputRequest() {

		final CodeExecuteRequest request = new CodeExecuteRequest();
		request.setLang("c");
		request.setId("S1");
		request.setSrcCode(C_SOURCE_CODE_NON_INPUT);

		return request;
	}

	/**
	 * 
	 */
	private static CodeExecuteRequest getJavaNonInputRequest() {

		final CodeExecuteRequest request = new CodeExecuteRequest();
		request.setId("S1");
		request.setLang("java");
		request.setSrcCode(JAVA_SOURCE_CODE_NON_INPUT);

		return request;
	}

	public static void executeJavaNonInputTest(
			final CodeExecuteServiceMgr codeExecuteServiceMgr) throws Exception {

		System.out.println(codeExecuteServiceMgr
				.compileAndExecute(getJavaNonInputRequest()));
	}

	public static void executeCNonInputTest(
			final CodeExecuteServiceMgr codeExecuteServiceMgr) throws Exception {

		System.out.println(codeExecuteServiceMgr
				.compileAndExecute(getCNonInputRequest()));
	}
}

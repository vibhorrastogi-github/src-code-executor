/**
 * Jul 27, 2014
 * CodeExecutorServiceMgrTest.java
 * 6:04:02 PM
 * vibhor
 */
package com.codeshare.codeexecutor.executor;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.codeshare.codeexecutor.common.bean.CodeExecuteRequest;

/**
 * @author vibhor
 * 
 */
public class CodeExecutorServiceMgrTest {

	private static final String JAVA_SOURCE_CODE_NON_INPUT = "public class MySrcCode { public static void main(String[] args) { System.out.println(\"Welcome Code-Share\"); new AnotherClass(); } } class AnotherClass {	public AnotherClass() {	System.out.println(\"Code from another class in same file\"); } }";

	private static final String JAVA_SOURCE_CODE_WITH_INPUT = "import java.util.Scanner; public class Solution { public static void main(String[] args) { final Scanner in = new Scanner(System.in); final int n = in.nextInt(); in.nextLine(); for (int i = 0; i < n; i++) { String nbrStr = in.nextLine(); String[] nbrStrArr = nbrStr.split(\" \"); int[] nbrArr = new int[nbrStrArr.length]; int j = 0; for (String s : nbrStrArr) { nbrArr[j] = Integer.valueOf(s).intValue(); j++; }	int maxNbr = getMax(nbrArr); System.out.println(maxNbr); } in.close(); } private static int getMax(final int[] nbrArr) { int maxNbr = nbrArr[0]; for (int i : nbrArr) { if (i > maxNbr) { maxNbr = i; } } return maxNbr;} }";

	private static final String C_SOURCE_CODE_NON_INPUT = "#include<stdio.h> #include<conio.h>\nvoid main() { int i=1/0; printf(\"C-Welcome to Code-Share\"); }";

	public static void main(String[] args) throws Exception {

		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"src-code-executor-test-ctx.xml");

		final CodeExecuteServiceMgr codeExecuteServiceMgr = ctx
				.getBean(CodeExecuteServiceMgr.class);

		executeJavaNonInputTest(codeExecuteServiceMgr);
		
		ctx.close();
	}

	public static void executeJavaNonInputTest(
			final CodeExecuteServiceMgr codeExecuteServiceMgr) throws Exception {
		final CodeExecuteRequest request = new CodeExecuteRequest();
		request.setId("1");
		request.setLang("java");
		request.setSrcCode(JAVA_SOURCE_CODE_NON_INPUT);

		System.out.println(codeExecuteServiceMgr.compileAndExecute(request));
	}

}

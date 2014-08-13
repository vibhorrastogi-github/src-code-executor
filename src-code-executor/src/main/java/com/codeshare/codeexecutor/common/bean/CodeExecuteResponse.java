/**
 * 
 */
package com.codeshare.codeexecutor.common.bean;

/**
 * @author vibhor
 * 
 */
public class CodeExecuteResponse {

	private String id;

	private String stdin;

	private String stdout;

	private String language;

	private long executionTime;

	private long memoryUtilized;

	private String error;

	private int handle;

	public CodeExecuteResponse(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStdin() {
		return stdin;
	}

	public void setStdin(String stdin) {
		this.stdin = stdin;
	}

	public String getStdout() {
		return stdout;
	}

	public void setStdout(String stdout) {
		this.stdout = stdout;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public long getMemoryUtilized() {
		return memoryUtilized;
	}

	public void setMemoryUtilized(long memoryUtilized) {
		this.memoryUtilized = memoryUtilized;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getHandle() {
		return handle;
	}

	public void setHandle(int handle) {
		this.handle = handle;
	}

	@Override
	public String toString() {
		return "CodeExecuteResponse [id=" + id + ", stdin=" + stdin
				+ ", stdout=" + stdout + ", language=" + language
				+ ", executionTime=" + executionTime + ", memoryUtilized="
				+ memoryUtilized + ", error=" + error + ", handle=" + handle
				+ "]";
	}

}

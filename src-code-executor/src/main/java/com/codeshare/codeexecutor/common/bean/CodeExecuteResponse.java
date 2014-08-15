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

	private int successHandle;

	private boolean isFileCreated = false;

	private boolean isCompiled = false;

	private boolean isExecuted = false;

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

	public int getSuccessHandle() {
		return successHandle;
	}

	public void setSuccessHandle(int successHandle) {
		this.successHandle = successHandle;
	}

	public boolean isFileCreated() {
		return isFileCreated;
	}

	public void setFileCreated(boolean isFileCreated) {
		this.isFileCreated = isFileCreated;
	}

	public boolean isCompiled() {
		return isCompiled;
	}

	public void setCompiled(boolean isCompiled) {
		this.isCompiled = isCompiled;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}

	@Override
	public String toString() {
		return "CodeExecuteResponse [id=" + id + ", stdin=" + stdin
				+ ", stdout=" + stdout + ", language=" + language
				+ ", executionTime=" + executionTime + ", memoryUtilized="
				+ memoryUtilized + ", error=" + error + ", handle=" + handle
				+ ", successHandle=" + successHandle + ", isFileCreated="
				+ isFileCreated + ", isCompiled=" + isCompiled
				+ ", isExecuted=" + isExecuted + "]";
	}

}

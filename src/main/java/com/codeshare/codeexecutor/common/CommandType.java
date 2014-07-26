/**
 * 
 */
package com.codeshare.codeexecutor.common;

/**
 * @author vibhor
 * 
 */
public enum CommandType {

	COMPILE("compile"), EXECUTE("execute");

	private String value;

	private CommandType(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static void main(String[] args) {
		System.out.println(CommandType.COMPILE.getValue());
	}
}

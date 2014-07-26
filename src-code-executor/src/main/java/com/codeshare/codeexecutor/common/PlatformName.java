/**
 * 
 */
package com.codeshare.codeexecutor.common;

/**
 * @author vibhor
 * 
 */
public enum PlatformName {

	WINDOWS("windows"), UNIX("unix"), LINUX("linux");

	private String value;

	private PlatformName(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static void main(String[] args) {
		System.out.println(PlatformName.WINDOWS.getValue());
	}
}

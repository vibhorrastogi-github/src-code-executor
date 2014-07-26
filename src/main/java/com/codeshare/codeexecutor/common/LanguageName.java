/**
 * 
 */
package com.codeshare.codeexecutor.common;

/**
 * @author vibhor
 *
 */
public enum LanguageName {

	JAVA("java"), C("c"), CPP("cpp");

	private String value;

	private LanguageName(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static void main(String[] args) {
		System.out.println(LanguageName.JAVA.getValue());
		System.out.println(LanguageName.valueOf("C").getValue());
	}
}

/**
 * 
 */
package com.codeshare.codeexecutor.common.bean;

/**
 * @author vibhor
 * 
 */
public class CodeExecuteRequest {

	private String id;

	private String lang;

	private String srcCode;

	private String stdin;

	public CodeExecuteRequest() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		if (lang != null) {
			this.lang = lang.trim().toLowerCase();
		}
	}

	public String getSrcCode() {
		return srcCode;
	}

	public void setSrcCode(String srcCode) {
		this.srcCode = srcCode;
	}

	public String getStdin() {
		return stdin;
	}

	public void setStdIn(String stdin) {
		this.stdin = stdin;
	}

	@Override
	public String toString() {
		return "ExecuteRequest [id=" + id + ", lang=" + lang + ", srcCode="
				+ srcCode + ", stdin=" + stdin + "]";
	}

}

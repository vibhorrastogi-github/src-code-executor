/**
 * 
 */
package com.codeshare.codeexecutor.commandbox;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vibhor
 * 
 */
@XmlRootElement
public class Language {

	private String name;

	private String compileCmd;

	private String executeCmd;

	private String patternInSrcCode;

	public String getName() {
		return name;
	}

	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	public String getCompileCmd() {
		return compileCmd;
	}

	public void setCompileCmd(String compileCmd) {
		this.compileCmd = compileCmd;
	}

	public String getExecuteCmd() {
		return executeCmd;
	}

	public void setExecuteCmd(String executeCmd) {
		this.executeCmd = executeCmd;
	}

	public String getPatternInSrcCode() {
		return patternInSrcCode;
	}

	public void setPatternInSrcCode(String patternInSrcCode) {
		this.patternInSrcCode = patternInSrcCode;
	}

	@Override
	public String toString() {
		return "Language [name=" + name + ", compileCmd=" + compileCmd
				+ ", executeCmd=" + executeCmd + ", patternInSrcCode="
				+ patternInSrcCode + "]";
	}

}

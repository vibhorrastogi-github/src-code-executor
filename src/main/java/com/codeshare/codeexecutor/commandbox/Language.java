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

	private String compileArg;

	private String executeCmd;

	private String executeArg;

	private boolean createFolder;

	private String generatedFileExtensions;

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

	public String getCompileArg() {
		return compileArg;
	}

	public void setCompileArg(String compileArg) {
		this.compileArg = compileArg;
	}

	public String getExecuteArg() {
		return executeArg;
	}

	public void setExecuteArg(String executeArg) {
		this.executeArg = executeArg;
	}

	public boolean isCreateFolder() {
		return createFolder;
	}

	public void setCreateFolder(boolean createFolder) {
		this.createFolder = createFolder;
	}

	public String getGeneratedFileExtensions() {
		return generatedFileExtensions;
	}

	public void setGeneratedFileExtensions(String generatedFileExtensions) {
		this.generatedFileExtensions = generatedFileExtensions;
	}

	@Override
	public String toString() {
		return "Language [name=" + name + ", compileCmd=" + compileCmd
				+ ", compileArg=" + compileArg + ", executeCmd=" + executeCmd
				+ ", executeArg=" + executeArg + ", createFolder="
				+ createFolder + ", generatedFileExtensions="
				+ generatedFileExtensions + "]";
	}

}

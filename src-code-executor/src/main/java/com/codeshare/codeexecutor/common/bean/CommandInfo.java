/**
 * 
 */
package com.codeshare.codeexecutor.common.bean;

/**
 * @author vibhor
 * 
 */
public class CommandInfo {

	private String cmd;

	private String arg;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getArg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}

	@Override
	public String toString() {
		return "CommandInfo [cmd=" + cmd + ", arg=" + arg + "]";
	}

}

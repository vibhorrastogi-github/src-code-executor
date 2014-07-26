/**
 * 
 */
package com.codeshare.codeexecutor.commandbox;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vibhor
 * 
 */
@XmlRootElement(name = "commandBox")
public class CommandBox {

	private Set<Language> language;

	public Set<Language> getLanguage() {
		return language;
	}

	public void setLanguage(Set<Language> language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "CommandBox [language=" + language + "]";
	}

}

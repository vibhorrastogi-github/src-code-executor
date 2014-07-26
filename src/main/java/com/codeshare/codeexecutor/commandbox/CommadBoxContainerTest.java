/**
 * 
 */
package com.codeshare.codeexecutor.commandbox;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.codeshare.codeexecutor.common.CommandType;

/**
 * @author vibhor
 * 
 */
public class CommadBoxContainerTest {

	public static void main(String[] args) throws JAXBException {
		// System.out.println(System.getProperty("os.name"));
		// System.getProperties().list(System.out);
		// generateCommandBox();
		System.out.println(new CommandBoxContainer().getComandInfo("java",
				CommandType.COMPILE));
	}

	public static void generateCommandBox() throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(CommandBox.class,
				Language.class);
		final Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(getCommandBox(), new File("cs_cb.xml"));
		System.out.println(true);
	}

	private static CommandBox getCommandBox() {
		CommandBox box = new CommandBox();

		Set<Language> language = new HashSet<Language>();
		box.setLanguage(language);

		Language lang = new Language();
		language.add(lang);

		lang.setCompileCmd("javac");
		lang.setCompileArg("");
		lang.setExecuteCmd("java");
		lang.setExecuteArg("");
		lang.setName("java");

		lang = new Language();
		language.add(lang);

		lang.setCompileCmd("tcc");
		lang.setCompileArg("");
		lang.setExecuteCmd("");
		lang.setExecuteArg("");
		lang.setName("c");
		return box;
	}
}

/**
 * 
 */
package com.codeshare.codeexecutor.commandbox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeshare.codeexecutor.common.CommandType;
import com.codeshare.codeexecutor.common.bean.CommandInfo;

/**
 * @author vibhor
 * 
 */
public class CommandBoxContainer {

	private static Logger LOGGER = LoggerFactory
			.getLogger(CommandBoxContainer.class);

	private Map<String, Language> lang_command_map;

	private Map<String, Set<String>> generated_file_extentions_map;

	public static void main(String[] args) {
		new CommandBoxContainer();
	}

	public CommandBoxContainer() {
		init();
	}

	private void init() {
		try {
			final JAXBContext jc = JAXBContext.newInstance(CommandBox.class,
					Language.class);
			final Unmarshaller u = jc.createUnmarshaller();
			final CommandBox commandBox = (CommandBox) u.unmarshal(this
					.getClass().getClassLoader()
					.getResourceAsStream("command_box/command_box.xml"));
			LOGGER.info("commandBox: {}", commandBox);

			initCommandInfoMap(commandBox);
			LOGGER.info("lang_command_map iniialized: {}", lang_command_map);

			initFileExtentionsMap(commandBox);
			LOGGER.info("lang_file_extentions_map iniialized: {}",
					generated_file_extentions_map);
		} catch (final JAXBException e) {
			throw new IllegalStateException("unable to Command box container",
					e);
		}
	}

	private void initFileExtentionsMap(final CommandBox commandBox) {
		generated_file_extentions_map = new HashMap<String, Set<String>>();
		final Set<Language> languages = commandBox.getLanguage();
		if (languages == null || languages.size() < 1) {
			return;
		}
		for (final Language lang : languages) {
			final String fileExtentions = lang.getGeneratedFileExtensions();
			final Set<String> fileExtentionSet = new HashSet<String>();
			for (String fileExtention : fileExtentions.split(",")) {
				fileExtentionSet.add(fileExtention);
			}
			generated_file_extentions_map.put(lang.getName().trim()
					.toLowerCase(), fileExtentionSet);
		}
	}

	public Set<String> getGeneratedFileExtentionSet(final String languageName) {

		final Set<String> fileExtentions = generated_file_extentions_map
				.get(languageName);

		if (fileExtentions == null) {
			throw new IllegalArgumentException("invalid languageName: "
					+ languageName);
		}
		return fileExtentions;
	}

	private void initCommandInfoMap(final CommandBox commandBox) {
		lang_command_map = new HashMap<String, Language>();
		final Set<Language> languages = commandBox.getLanguage();
		if (languages == null || languages.size() < 1) {
			return;
		}
		for (final Language lang : languages) {
			lang_command_map.put(lang.getName().trim().toLowerCase(), lang);
		}
	}

	public CommandInfo getComandInfo(final String languageName,
			final CommandType commandType) {
		if (!lang_command_map.containsKey(languageName)) {
			throw new IllegalArgumentException("invalid languageName: "
					+ languageName);
		}
		final Language lang = lang_command_map.get(languageName);
		final CommandInfo commandInfo = new CommandInfo();

		switch (commandType) {
		case COMPILE:
			commandInfo.setCmd(lang.getCompileCmd());
			commandInfo.setArg(lang.getCompileArg());
			break;
		case EXECUTE:
			commandInfo.setCmd(lang.getExecuteCmd());
			commandInfo.setArg(lang.getExecuteArg());
			break;
		default:
			throw new IllegalArgumentException("invalid commandType: "
					+ commandType);
		}
		return commandInfo;
	}

	public Language getLanguage(final String languageName) {
		final Language lang = lang_command_map.get(languageName);
		return lang;
	}

	public boolean isValidLanguage(final String lang) {
		return lang_command_map.containsKey(lang);
	}

	public Set<String> getValidLanguages() {
		return lang_command_map.keySet();
	}

}
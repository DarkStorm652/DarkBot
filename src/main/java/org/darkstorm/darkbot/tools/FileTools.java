package org.darkstorm.darkbot.tools;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

public class FileTools {
	public static final String DEFAULT_DIR = "%home%";
	public static final String CURRENT_DIR = "./";

	private FileTools() {
	}

	public static File getDirectoryFormatted(String directory) {
		String formattedDirectory = directory;
		formattedDirectory = replaceDefaultDirs(directory);
		return new File(formattedDirectory).getAbsoluteFile();
	}

	private static String replaceDefaultDirs(String directory) {
		String formattedDirectory = directory;
		String directoryLowerCase = directory.toLowerCase();
		while(directoryLowerCase.contains(DEFAULT_DIR)) {
			String defaultDirectory = getDefaultDirectory();
			int defaultDirIndex = directoryLowerCase.indexOf(DEFAULT_DIR);
			String toReplace = formattedDirectory.substring(defaultDirIndex,
					DEFAULT_DIR.length());
			formattedDirectory = replace(formattedDirectory, toReplace, defaultDirectory);
			directoryLowerCase = replace(directoryLowerCase, DEFAULT_DIR, defaultDirectory);
		}
		return formattedDirectory;
	}
	
	private static String replace(String modifying, String original, String replacement) {
		String modified = modifying;
		int index = modifying.indexOf(original);
		if(index > -1)
			modified = modifying.substring(0, index) + replacement + modifying.substring(index + original.length());
		return modified;
	}

	public static String getDefaultDirectory() {
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File defaultDirectory = fileSystemView.getDefaultDirectory();
		return defaultDirectory.getAbsolutePath();
	}
}

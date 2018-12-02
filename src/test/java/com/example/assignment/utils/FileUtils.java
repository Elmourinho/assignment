package com.example.assignment.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.util.ResourceUtils;

public class FileUtils {

	public static String readResourceFileAsString(String path) {
		try {
			File file = ResourceUtils.getFile(path);
			return new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			throw new IllegalStateException("failed to read resource file.", e);
		}
	}

}

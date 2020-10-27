package ru.resql;

import java.io.*;
import java.util.Properties;

public class TestUtils {
	private TestUtils() { }

	public static Properties loadProperties(Class<?> clazz, String propertyFileName) throws IOException {
		Properties props = new Properties();
		String pathToRoot = clazz.getPackage().getName().replace(".", "/").replaceAll("[^/]+", "..");
		InputStream is = clazz.getResourceAsStream(pathToRoot + "/" + propertyFileName);
		props.load(is);
		return props;
	}
}

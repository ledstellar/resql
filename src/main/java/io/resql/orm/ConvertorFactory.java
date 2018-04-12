package io.resql.orm;

import java.lang.reflect.Parameter;

/**
 * Find for and load all available convertors to be used for ORM data mapping
 */
public class ConvertorFactory {
	ConvertorFactory() {

	}

	public Convertor get(int sqlType, Class<?> dataFieldClass) {
		// TODO: implement
		return null;
	}

	public boolean isExists(int sqlType, Class<?> dataFieldClass) {
		// TODO: implement
		return false;
	}
}

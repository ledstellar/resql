package io.resql.orm;

import io.resql.orm.converters.Converter;

/**
 * Find for and load all available converters to be used for ORM data mapping
 */
public class ConverterFactory {
	ConverterFactory() {

	}

	public Converter get(int sqlType, Class<?> dataFieldClass) {
		// TODO: implement
		return null;
	}

	public boolean isExists(int sqlType, Class<?> dataFieldClass) {
		// TODO: implement
		return false;
	}
}
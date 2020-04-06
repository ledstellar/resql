package ru.resql.orm.converters;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import ru.resql.orm.converters.instances.ConverterFrame;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Find for and load all available converters for DB column types
 */
@Slf4j
public class ConverterFrames {
	ArrayList<ConverterFrame<?>> converters = new ArrayList<>();

	public ConverterFrames() {
		Reflections reflections = new Reflections(ConverterFrame.class.getPackage().getName());
		//noinspection rawtypes
		for(Class<? extends ConverterFrame> converterClass : reflections.getSubTypesOf(ConverterFrame.class)) {
			try {
				converters.add(converterClass.getConstructor().newInstance());
			} catch (Exception e) {
				log.error("Cannot instantiate converter of class " + converterClass, e);
			}
		}
	}

	public <ResultType> Converter<ResultType> getConverter(Class<?> destClassType, String destDescription, String columnName, int columnSqlType,
	 String columnTypeName) throws SQLException {
		for (ConverterFrame<?> converterFrame : converters) {
			Converter<?> converter = converterFrame.getConverter(this, destClassType, destDescription, columnName, columnSqlType, columnTypeName);
			if (converter != null) {
				//noinspection unchecked
				return (Converter<ResultType>)converter;
			}
		}
		return null;
	}
}

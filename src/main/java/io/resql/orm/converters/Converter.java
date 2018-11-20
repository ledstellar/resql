package io.resql.orm.converters;

import java.lang.reflect.Field;

/** Converter from database to object field data types and vice versa */
public interface Converter {
	/**
	 * DB column type this converter can be applied to
	 * @return database specific column type name
	 */
	String getDbColumnType();

	/**
	 * ORM class field type this converter can be applied to
	 * @return ORM field class
	 */
	Class<?> getFieldType();

	FromDbConverter fromDb(int columnIndex, Field field);

	ToDbTypeConverter toDb(int paramIndex, Field field);
}

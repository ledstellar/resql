package ru.resql.orm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import ru.resql.orm.converters.ConverterFrames;

import java.sql.*;

/**
 * ORM class access implementation.
 * Its ok for ORM class to have more class fields than supplied by query. However it's not ok when supplied query contains more
 * data columns than ORM class fields. If constructor accessor is used then constructor with exact parameters isNamesMatch exists.
 *
 * @param <ElementType> ORM class type
 */
public abstract class Accessor<ElementType> {
	/**
	 * Create new ORM class access implementation. Use either factory or targetClass param
	 * TODO: describe member resolution rules in details
	 * @param targetClass ORM class. When set then access will scan ORM class for appropriate constructor
	 */
	static <T> Accessor<T> newInstance(
		Logger log, ResultSetMetaData resultSetMetaData, boolean isOptionalFields, Class<T> targetClass, ConverterFrames converterFrames
	) throws SQLException {
			return new FieldDirectAccessor<>(log, resultSetMetaData, isOptionalFields, targetClass, converterFrames);
// TODO: implement
//			return new ConstructorAccessor<>(resultSetColumnTypes, targetClass, converterFrames);
	}

	String getColumnDescription(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		return resultSetMetaData.getColumnName(columnIndex) + ' ' + resultSetMetaData.getColumnTypeName(columnIndex);
	}

	static boolean isNamesMatch(String columnName, String fieldName) {
		// TODO: implement more smart algorithm and make it customizable
		return columnName.equals(fieldName);
	}

	/**
	 * Create or get new instance of ORM class and init it with resultset's current record. ORM class instance can be supplied by ORM class supplier
	 * or created by accessor using appropriate class constructor
	 *
	 * @param element element to be initialized
	 * @param resultSet opened resultset to init ORM class instance
	 */
	public abstract void init(ElementType element, ResultSet resultSet);
}

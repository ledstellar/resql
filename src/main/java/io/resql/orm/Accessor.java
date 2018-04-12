package io.resql.orm;

import io.resql.SqlException;
import org.slf4j.*;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * ORM class access implementation.
 * Its ok for ORM class to have more class fields than supplied by query. However it's not ok when supplied query contains more
 * data columns than ORM class fields. If constructor accessor is used then constructor with exact parameters isNamesMatch exists.
 * @param <T> ORM class type
 */
public class Accessor<T> {
	private static final Logger log = LoggerFactory.getLogger( Accessor.class );

	/**
	 * Create new ORM class access implementation. Use either factory or targetClass param
	 * @param metaData metadata of executed select query
	 * @param factory factory for ORM class. When set then accessor will use direct access to class' members.
	 * TODO: describe member resolution rules in details
	 * @param targetClass ORM class. When set then access will scan ORM class for appropriate constructor
	 */
	static <T> Accessor<T> newInstance(ResultSetMetaData metaData, Supplier<T> factory, Class<T> targetClass, ConvertorFactory convertorFactory) throws
		SQLException {
		if ( factory != null ) {
			return new FieldDirectAccessor<>(metaData, factory);
		} else {
			return new ConstructorAccessor<>(metaData, targetClass, convertorFactory);
		}
	}

	boolean isConvertorAvailable( int columnType, Class<?> fieldClass ) {
		return findConvertor(columnType, fieldClass) != null;
	}

	Convertor findConvertor( int columnType, Class<?> fieldClass ) {
		return null; 	// TODO: implement
	}

	String getColumnDescription( ResultSetMetaData metaData, int columnIndex ) throws SQLException {
		return metaData.getColumnName( columnIndex ) + ' ' + metaData.getColumnType( columnIndex );	// TODO: implement more accurately
	}

	static boolean isNamesMatch(String columnName, String fieldName ) {
		return columnName.equals( fieldName );	// TODO: implement more smart algorithm and make it customizable
	}

	/**
	 * Create or get new instance of ORM class and init it with resultset's current record. ORM class instance can be supplied by ORM class supplier
	 * or created by accessor using appropriate class constructor
	 * @param resultSet opened resultset to init ORM class instance
	 * @return initialized ORM class instance
	 */
	T get(ResultSet resultSet) {
		// TODO: implement
		return null;
	}
}

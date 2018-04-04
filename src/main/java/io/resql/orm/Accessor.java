package io.resql.orm;

import io.resql.SqlException;
import org.slf4j.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * ORM class access implementation.
 * Its ok for ORM class to have more class fields than supplied by query. However it's not ok when supplied query contains more
 * data columns than ORM class fields. If constructor accessor is used then constructor with exact parameters match exists.
 * @param <T> ORM class type
 */
public class Accessor<T> {
	private static final Logger log = LoggerFactory.getLogger( Accessor.class );

	private Convertor[] convertors;

	/**
	 * Create new ORM class access implementation. Use either factory or targetClass param
	 * @param metaData metadata of executed select query
	 * @param factory factory for ORM class. When set then accessor will use direct access to class' members.
	 * TODO: describe member resolution rules in details
	 * @param targetClass ORM class. When set then access will scan ORM class for appropriate constructor
	 */
	Accessor( ResultSetMetaData metaData, Supplier< T > factory, Class< T > targetClass ) throws SQLException {
		ArrayList<String> unmappedColumns = null;
		if ( factory != null ) {
			T ormInstance = factory.get();
			Class<?> ormClass = ormInstance.getClass();
			convertors = new Convertor[ metaData.getColumnCount() ];
			for ( int columnIndex = 1; columnIndex <= metaData.getColumnCount(); ++ columnIndex ) {
				Field field = findMatchedField( metaData.getColumnName( columnIndex ), ormClass );
				if ( field == null ) {
					if ( unmappedColumns == null ) {
						unmappedColumns = new ArrayList<>();
					}
					unmappedColumns.add( getColumnDescription( metaData, columnIndex ) );
				} else {
					convertors[ columnIndex ] = findConvertor( metaData.getColumnType(columnIndex), field );
				}
			}
			if ( unmappedColumns != null ) {
				throw new SqlException( "Next resultset columns was not mapped: " + String.join( ", ", unmappedColumns ) );
			}
		} else {
			// TODO: implements constructor based mappings
		}
	}

	private Convertor findConvertor( int columnType, Field field ) {
		return null; 	// TODO: implement
	}

	private static String getColumnDescription( ResultSetMetaData metaData, int columnIndex ) throws SQLException {
		return metaData.getColumnName( columnIndex ) + ' ' + metaData.getColumnType( columnIndex );	// TODO: implement more accurately
	}

	private Field findMatchedField( String columnName, Class<?> ormClass ) {
		do {
			for ( Field field : ormClass.getDeclaredFields() ) {
				if ( match( columnName, field.getName() ) ) {
					return field;
				}
			}
			ormClass = ormClass.getSuperclass();
		}
		while (ormClass != null);
		return null;
	}

	private boolean match( String columnName, String fieldName ) {
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

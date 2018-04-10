package io.resql.orm;

import io.resql.SqlException;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.function.Supplier;

class FieldDirectAccessor<T> extends Accessor<T> {
	private Convertor[] convertors;

	FieldDirectAccessor(ResultSetMetaData metaData, Supplier<T> factory) throws SQLException {
		T ormInstance = factory.get();
		Class<?> ormClass = ormInstance.getClass();
		convertors = new Convertor[ metaData.getColumnCount() ];
		ArrayList<String> unmappedColumns = null;
		for ( int columnIndex = 1; columnIndex <= metaData.getColumnCount(); ++ columnIndex ) {
			Field field = findMatchedField( metaData.getColumnName( columnIndex ), ormClass );
			if ( field == null ) {
				if ( unmappedColumns == null ) {
					unmappedColumns = new ArrayList<>();
				}
				unmappedColumns.add( getColumnDescription( metaData, columnIndex ) );
			} else {
				convertors[ columnIndex ] = null; // TODO: implement findConvertor( metaData.getColumnType(columnIndex), field );
			}
		}
		if ( unmappedColumns != null ) {
			throw new SqlException( "Next resultset columns was not mapped: " + String.join( ", ", unmappedColumns ) );
		}
	}

	private Field findMatchedField( String columnName, Class<?> ormClass ) {
		do {
			for ( Field field : ormClass.getDeclaredFields() ) {
				if ( isNamesMatch( columnName, field.getName() ) ) {
					return field;
				}
			}
			ormClass = ormClass.getSuperclass();
		}
		while (ormClass != null);
		return null;
	}
}

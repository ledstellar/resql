package io.resql.orm;

import io.resql.SqlException;
import io.resql.orm.converters.Converter;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

class FieldDirectAccessor<T> extends Accessor<T> {
	private Converter[] convertors;

	FieldDirectAccessor(Map<String, Integer> resultSetColumnTypes, Supplier<T> factory) throws SQLException {
		T ormInstance = factory.get();
		Class<?> ormClass = ormInstance.getClass();
		convertors = new Converter[resultSetColumnTypes.size()];
		ArrayList<String> unmappedColumns = null;
		int columnIndex = 0;
		for (Map.Entry<String, Integer> columnEntry : resultSetColumnTypes.entrySet()) {
			Field field = findMatchedField(columnEntry.getKey(), ormClass);
			if (field == null) {
				if (unmappedColumns == null) {
					unmappedColumns = new ArrayList<>();
				}
				unmappedColumns.add(getColumnDescription(columnEntry.getKey(), columnEntry.getValue()));
			} else {
				convertors[columnIndex++] = null; // TODO: implement findConvertor( metaData.getColumnType(columnIndex), field );
			}
		}
		if (unmappedColumns != null) {
			throw new SqlException("Next resultset columns was not mapped: " + String.join(", ", unmappedColumns));
		}
	}

	private Field findMatchedField(String columnName, Class<?> ormClass) {
		do {
			for (Field field : ormClass.getDeclaredFields()) {
				if (isNamesMatch(columnName, field.getName())) {
					return field;
				}
			}
			ormClass = ormClass.getSuperclass();
		}
		while (ormClass != null);
		return null;
	}
}

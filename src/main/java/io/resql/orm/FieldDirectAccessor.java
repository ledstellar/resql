package io.resql.orm;

import io.resql.SqlException;
import io.resql.orm.converters.*;
import org.slf4j.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

class FieldDirectAccessor<T> extends Accessor<T> {
	private FromDbConverter[] converters;
	private Supplier<T> factory;
	private static final Logger log = LoggerFactory.getLogger(FieldDirectAccessor.class);

	FieldDirectAccessor(LinkedHashMap<String, String> resultSetColumnTypes, Supplier<T> factory) throws SQLException {
		this.factory = factory;
		T ormInstance = factory.get();
		Class<?> ormClass = ormInstance.getClass();
		converters = new FromDbConverter[resultSetColumnTypes.size()];
		ArrayList<String> unmappedColumns = null;
		int columnIndex = 0;
		for (Map.Entry<String, String> columnEntry : resultSetColumnTypes.entrySet()) {
			Field field = findMatchedField(columnEntry.getKey(), ormClass);
			if (field == null) {
				if (unmappedColumns == null) {
					unmappedColumns = new ArrayList<>();
				}
				unmappedColumns.add(getColumnDescription(columnEntry.getKey(), columnEntry.getValue()));
			} else {
				field.setAccessible(true);
				FromDbConverter converter = findConvertor(columnEntry.getValue(), field.getType()).fromDb(columnIndex + 1, field);
				log.debug("Found converter from DB to field {}: {}", field, converter);
				converters[columnIndex ++] = converter;
			}
		}
		if (unmappedColumns != null) {
			throw new SqlException("Next resultset columns was not mapped: " + String.join(", ", unmappedColumns));
		}
	}

	private Field findMatchedField(String columnName, Class<?> ormClass) {
		do {
			for (Field field : ormClass.getDeclaredFields()) {
				if (isNamesMatch(columnName.toLowerCase(), field.getName().toLowerCase())) {
					return field;
				}
			}
			ormClass = ormClass.getSuperclass();
		}
		while (ormClass != null);
		return null;
	}

	@Override
	public T get(ResultSet resultSet) {
		T instance = factory.get();
		int i = 1;
		try {
			for (FromDbConverter converter : converters) {
				converter.convert(resultSet, instance);
			}
		} catch (Exception e) {
			throw new SqlException("Cannot set up class " + instance.getClass() + " instance", e);  // FIXME: need more details here
		}
		return instance;
	}
}

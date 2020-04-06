package ru.resql.orm;

import org.slf4j.Logger;
import ru.resql.SqlException;
import ru.resql.orm.collators.*;
import ru.resql.orm.converters.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import static ru.resql.DbManager.RESQL_MAPPINGS;

class FieldDirectAccessor<ElementType> extends Accessor<ElementType> {
	private final Converter<?>[] converters;
	private final Field[] fields;
	private final static ColumnCollator columnCollator = new PossibleCamelCaseCollator();

	FieldDirectAccessor(
		Logger log, ResultSetMetaData resultSetMetaData, Class<ElementType> ormClass, ConverterFrames converterFrames
	) throws SQLException {
		int columnCount = resultSetMetaData.getColumnCount();
		converters = new Converter[columnCount];
		fields = new Field[columnCount];
		ArrayList<String> unmappedColumns = null;
		HashMap<String, Field> allFields = findFields(ormClass);
		StringBuilder ormMappings = null;
		if (log.isDebugEnabled(RESQL_MAPPINGS)) {
			ormMappings = new StringBuilder("\tfollowing converters are found for class ").append(ormClass.getName()).append(':');
		}
		for (int columnIndex = 1; columnIndex <= columnCount; ++ columnIndex) {
			String columnName = resultSetMetaData.getColumnName(columnIndex);
			String fieldName = columnCollator.collate(columnName, allFields.keySet());
			if (fieldName == null) {
				if (unmappedColumns == null) {
					unmappedColumns = new ArrayList<>();
				}
				unmappedColumns.add(getColumnDescription(resultSetMetaData, columnIndex));
			} else {
				Field field = allFields.get(fieldName);
				field.setAccessible(true);
				Converter<?> converter = converterFrames.getConverter(
					field.getType(),
					field.getType().getSimpleName() + " " + field.getName(),
					resultSetMetaData.getColumnTypeName(columnIndex) + " " + resultSetMetaData.getColumnName(columnIndex),
					resultSetMetaData.getColumnType(columnIndex), resultSetMetaData.getColumnTypeName(columnIndex)
				);
				if (converter == null) {
					throw new SqlException(
						"No converter can be found for field " + field.getName()
						+ " from " + getColumnDescription(resultSetMetaData, columnIndex)
					);
				}
				if (ormMappings != null) {
					ormMappings.append('\n').append(converter.toString());
				}
				fields[columnIndex - 1] = field;
				converters[columnIndex - 1] = converter;
			}
		}
		if (ormMappings != null) {
			log.debug(RESQL_MAPPINGS, ormMappings.toString());
		}
		if (unmappedColumns != null) {
			throw new SqlException("Next column(s) was not mapped: " + String.join(", ", unmappedColumns));
		}
	}

	private HashMap<String, Field> findFields(Class<?> ormClass) {
		HashMap<String, Field> allFields = new HashMap<>();
		do {
			for (Field field : ormClass.getDeclaredFields()) {
				allFields.put(field.getName(), field);
			}
			ormClass = ormClass.getSuperclass();
		} while (ormClass != null);
		return allFields;
	}

	@Override
	public void init(ElementType element, ResultSet resultSet) {
		int columnIndex = 0;
		try {
			for (Converter<?> converter : converters) {
				// each field index is one less than related column index
				fields[columnIndex++].set(element, converter.convert(resultSet.getObject(columnIndex)));
			}
		} catch (IllegalAccessException | IllegalArgumentException | SQLException e) {
			// TODO: separate exception handling
			throw new SqlException("Cannot init class " + element.getClass() + " instance", e);  // FIXME: need more details here
		}
	}
}

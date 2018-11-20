package io.resql.orm.converters;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;

@SuppressWarnings("unused")
public class HstoreHashMapConverter implements Converter {
	@Override
	public String getDbColumnType() {
		return "hstore";
	}

	@Override
	public Class<?> getFieldType() {
		return HashMap.class;
	}

	private static class HstoreToHashMapConverter extends FromDbConverterBase {
		HstoreToHashMapConverter(int columnIndex, Field field) {
			super(columnIndex, field);
		}

		@Override
		public void convert(ResultSet rs, Object classInstance) throws SQLException, IllegalAccessException {
			field.set(classInstance, rs.getObject(columnIndex));
		}
	}

	private static class HashMapToHstoreConverter extends ToDbConverterBase {
		HashMapToHstoreConverter(int paramIndex, Field field) {
			super(paramIndex, field);
		}

		@Override
		public void convert(PreparedStatement rs, Object classInstance) throws SQLException, IllegalAccessException {
			rs.setObject(paramIndex, field.get(classInstance));
		}
	}


	@Override
	public FromDbConverter fromDb(int columnIndex, Field field) {
		return new HstoreToHashMapConverter(columnIndex, field);
	}

	@Override
	public ToDbTypeConverter toDb(int paramIndex, Field field) {
		return new HashMapToHstoreConverter(paramIndex, field);
	}
}

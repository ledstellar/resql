package io.resql.orm.converters;

import java.lang.reflect.Field;
import java.sql.*;

@SuppressWarnings("unused")
public class Int4IntConverter implements Converter {
	@Override
	public String getDbColumnType() {
		return "int4";
	}

	@Override
	public Class<?> getFieldType() {
		return int.class;
	}

	private static class Int4ToIntConverter extends FromDbConverterBase {
		Int4ToIntConverter(int columnIndex, Field field) {
			super(columnIndex, field);
		}

		@Override
		public void convert(ResultSet rs, Object classInstance) throws SQLException, IllegalAccessException {
			field.set(classInstance, rs.getInt(columnIndex));
		}
	}

	@Override
	public FromDbConverter fromDb(int columnIndex, Field field) {
		return new Int4ToIntConverter(columnIndex, field);
	}

	private static class IntToInt4Converter extends ToDbConverterBase {
		IntToInt4Converter(int paramIndex, Field field) {
			super(paramIndex, field);
		}

		@Override
		public void convert(PreparedStatement rs, Object classInstance) throws SQLException, IllegalAccessException {
			rs.setInt(paramIndex, (int)field.get(classInstance));
		}
	}

	@Override
	public ToDbTypeConverter toDb(int paramIndex, Field field) {
		return new IntToInt4Converter(paramIndex, field);
	}
}

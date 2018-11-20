package io.resql.orm.converters;

import java.lang.reflect.Field;
import java.sql.*;

@SuppressWarnings("unused")
public class Float8DoubleConverter implements Converter {
	@Override
	public String getDbColumnType() {
		return "float8";
	}

	@Override
	public Class<?> getFieldType() {
		return double.class;
	}

	private static class Float8ToDoubleConverter extends FromDbConverterBase {
		Float8ToDoubleConverter(int columnIndex, Field field) {
			super(columnIndex, field);
		}

		@Override
		public void convert(ResultSet rs, Object classInstance) throws SQLException, IllegalAccessException {
			field.set(rs.getDouble(columnIndex), classInstance);
		}
	}

	private static class DoubleToFloat8Converter extends ToDbConverterBase {
		DoubleToFloat8Converter(int columnIndex, Field field) {
			super(columnIndex, field);
		}

		@Override
		public void convert(PreparedStatement rs, Object classInstance) throws SQLException, IllegalAccessException {
			rs.setDouble(paramIndex, (double)field.get(classInstance));
		}
	}

	@Override
	public FromDbConverter fromDb(int columnIndex, Field field) {
		return new Float8ToDoubleConverter(columnIndex, field);
	}

	@Override
	public ToDbTypeConverter toDb(int paramIndex, Field field) {
		return new DoubleToFloat8Converter(paramIndex, field);
	}
}

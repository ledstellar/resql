package io.resql.orm.converters;

import java.lang.reflect.Field;
import java.sql.*;

public class IntegerConverter implements Converter<Integer> {
	@Override
	public int getDbColumnType() {
		return 0;   // TODO: implement
	}

	@Override
	public Class<Integer> getFieldType() {
		return null;
	}

	@Override
	public void setDbValueToClass(Object ormInstance, Field ormField, ResultSet rs, int columnIndex) {

	}

	@Override
	public void setClassValueToDb(Object ormInstance, Field ormField, PreparedStatement ps, int columnIndex) {

	}
}

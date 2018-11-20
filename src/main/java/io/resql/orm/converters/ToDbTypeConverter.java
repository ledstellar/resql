package io.resql.orm.converters;

import java.lang.reflect.Field;
import java.sql.*;

@FunctionalInterface
public interface ToDbTypeConverter {
	void convert(PreparedStatement rs, Object classInstance) throws SQLException, IllegalAccessException;
}

package io.resql.orm.converters;

import java.lang.reflect.Field;
import java.sql.*;

@FunctionalInterface
public interface FromDbConverter {
	void convert(ResultSet rs, Object classInstance) throws SQLException, IllegalAccessException;
}

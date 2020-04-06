package ru.resql.orm.converters;

import java.sql.*;

@FunctionalInterface
public interface FromDbConverter {
	void convert(ResultSet rs, Object classInstance) throws SQLException, IllegalAccessException;
}

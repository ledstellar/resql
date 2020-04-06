package ru.resql.orm.converters;

import java.sql.*;

@FunctionalInterface
public interface Converter<ResultType> {
	ResultType convert(Object columnValue) throws SQLException, ConverterException;
}

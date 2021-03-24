package ru.resql.orm.converters.todb;

import ru.resql.orm.converters.ConverterException;

import java.sql.SQLException;

@FunctionalInterface
public interface Converter<ResultType> {
	ResultType convert(Object columnValue) throws SQLException, ConverterException;
}

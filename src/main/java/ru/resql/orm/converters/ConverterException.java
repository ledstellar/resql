package ru.resql.orm.converters;

import ru.resql.SqlException;

public class ConverterException extends SqlException {
	public ConverterException(Object value, String message) {
		super(message);
	}
}

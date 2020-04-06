package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.SQLException;

public class DirectArrayConverter extends ConverterImpl<Object> {
	public DirectArrayConverter(String columnDescription, String fieldDescrption) {
		super(columnDescription, fieldDescrption);
	}

	@Override
	public Object convert(Object columnValue) throws SQLException, ConverterException {
		return ((java.sql.Array)columnValue).getArray();
	}
}

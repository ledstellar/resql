package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.*;

public class ArrayOfPrimitiveBooleanConverter  extends ConverterImpl<Object> {
	private final Converter<?> nestedConverter;

	public ArrayOfPrimitiveBooleanConverter(String columnDescription, String fieldDescription, Converter<?> nestedConverter) {
		super(columnDescription, fieldDescription);
		this.nestedConverter = nestedConverter;
	}

	@Override
	public Object convert(Object columnValue) throws SQLException, ConverterException {
		Object[] dbArray = (Object[])((Array)columnValue).getArray();
		boolean[] retArray = (boolean[])java.lang.reflect.Array.newInstance(boolean.class, dbArray.length);
		for (int i = dbArray.length - 1; i >= 0; -- i) {
			retArray[i] = (boolean)(Boolean)nestedConverter.convert(dbArray[i]);
		}
		return retArray;
	}
}


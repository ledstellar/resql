package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.*;

public class ArrayOfPrimitiveLongConverter extends ConverterImpl<Object> {
	private final Converter<?> nestedConverter;

	public ArrayOfPrimitiveLongConverter(String columnDescription, String fieldDescription, Converter<?> nestedConverter) {
		super(columnDescription, fieldDescription);
		this.nestedConverter = nestedConverter;
	}

	@Override
	public Object convert(Object columnValue) throws SQLException, ConverterException {
		Object[] dbArray = (Object[])((Array)columnValue).getArray();
		long[] retArray = (long[])java.lang.reflect.Array.newInstance(long.class, dbArray.length);
		for (int i = dbArray.length - 1; i >= 0; -- i) {
			retArray[i] = (long)(Long)nestedConverter.convert(dbArray[i]);
		}
		return retArray;
	}
}

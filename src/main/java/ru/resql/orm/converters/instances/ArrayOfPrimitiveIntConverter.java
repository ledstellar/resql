package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.*;

public class ArrayOfPrimitiveIntConverter extends ConverterImpl<Object> {
	private final Converter<?> nestedConverter;

	public ArrayOfPrimitiveIntConverter(String columnDescription, String fieldDescription, Converter<?> nestedConverter) {
		super(columnDescription, fieldDescription);
		this.nestedConverter = nestedConverter;
	}

	@Override
	public int[] convert(Object columnValue) throws SQLException, ConverterException {
		Object[] dbArray = (Object[])((Array)columnValue).getArray();
		int[] retArray = (int[])java.lang.reflect.Array.newInstance(int.class, dbArray.length);
		for (int i = dbArray.length - 1; i >= 0; -- i) {
			retArray[i] = (int)(Integer)nestedConverter.convert(dbArray[i]);
		}
		return retArray;
	}
}

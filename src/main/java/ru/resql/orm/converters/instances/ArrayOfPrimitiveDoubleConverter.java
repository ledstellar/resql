package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.*;

public class ArrayOfPrimitiveDoubleConverter extends ConverterImpl<Object> {
	private final Converter<?> nestedConverter;

	public ArrayOfPrimitiveDoubleConverter(String columnDescription, String fieldDescription, Converter<?> nestedConverter) {
		super(columnDescription, fieldDescription);
		this.nestedConverter = nestedConverter;
	}

	@Override
	public Object convert(Object columnValue) throws SQLException, ConverterException {
		Object[] dbArray = (Object[])((Array)columnValue).getArray();
		double[] retArray = (double[])java.lang.reflect.Array.newInstance(double.class, dbArray.length);
		for (int i = dbArray.length - 1; i >= 0; -- i) {
			retArray[i] = (double)(Double)nestedConverter.convert(dbArray[i]);
		}
		return retArray;
	}
}

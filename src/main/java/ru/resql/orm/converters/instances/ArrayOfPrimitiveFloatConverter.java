package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.*;

public class ArrayOfPrimitiveFloatConverter extends ConverterImpl<Object> {
	private final Converter<?> nestedConverter;

	public ArrayOfPrimitiveFloatConverter(String columnDescription, String fieldDescription, Converter<?> nestedConverter) {
		super(columnDescription, fieldDescription);
		this.nestedConverter = nestedConverter;
	}

	@Override
	public Object convert(Object columnValue) throws SQLException, ConverterException {
		Object[] dbArray = (Object[])((Array)columnValue).getArray();
		float[] retArray = (float[])java.lang.reflect.Array.newInstance(float.class, dbArray.length);
		for (int i = dbArray.length - 1; i >= 0; -- i) {
			retArray[i] = (float)(Float)nestedConverter.convert(dbArray[i]);
		}
		return retArray;
	}
}


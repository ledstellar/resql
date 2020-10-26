package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.*;

public class ArrayOfCustomTypeConverter  extends ConverterImpl<Object> {
	private final Converter<Object> nestedConverter;
	private final Class<?> componentType;

	public ArrayOfCustomTypeConverter(String columnDescription, String fieldDescription, Converter<Object> nestedConverter, Class<?> componentType) {
		super(columnDescription, fieldDescription);
		this.nestedConverter = nestedConverter;
		this.componentType = componentType;
	}

	@Override
	public Object convert(Object columnValue) throws SQLException, ConverterException {
		Object[] dbArray = (Object[])((Array)columnValue).getArray();
		Object[] retArray = (Object[])java.lang.reflect.Array.newInstance(componentType, dbArray.length);
		for (int i = dbArray.length - 1; i >= 0; -- i) {
			retArray[i] = nestedConverter.convert(dbArray[i]);
		}
		return retArray;
	}
}

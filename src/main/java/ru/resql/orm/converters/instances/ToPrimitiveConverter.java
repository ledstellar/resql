package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

public class ToPrimitiveConverter extends ConverterImpl<Object> {
	public ToPrimitiveConverter(String columnDescription, String fieldDescrption) {
		super(columnDescription, fieldDescrption);
	}

	@Override
	public Object convert(Object columnValue) throws ConverterException {
		if (columnValue == null) {
			throw new ConverterException(
				null,
				"Column " + columnDescription + " has NULL value but "
				+ fieldDescription + " has primitive type");
		}
		return columnValue;
	}
}

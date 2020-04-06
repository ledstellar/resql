package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

public class IdentityConverter extends ConverterImpl<Object> {
	public IdentityConverter(String columnDescription, String fieldDescrption) {
		super(columnDescription, fieldDescrption);
	}

	@Override
	public Object convert(Object columnValue) {
		return columnValue;
	}
}

package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

@SuppressWarnings("rawtypes")
public class TextTypeToNamedEnumConvertor implements Converter<Enum<?>> {
	private final Class<Enum> enumType;

	public TextTypeToNamedEnumConvertor(Class<Enum> enumType) {
		this.enumType = enumType;
	}

	@Override
	public Enum<?> convert(Object columnValue) throws ConverterException {
		//noinspection unchecked
		return Enum.valueOf(enumType, columnValue.toString());
	}
}

package ru.resql.orm.converters.instances;

import ru.resql.SqlException;
import ru.resql.orm.converters.*;

public class IntegralTypeToOrdinalEnumConvertor implements Converter<Enum<?>> {
	private final Enum<?>[] enumValues;

	public IntegralTypeToOrdinalEnumConvertor(Class<Enum<?>> enumType) {
		enumValues = enumType.getEnumConstants();
	}

	@Override
	public Enum<?> convert(Object columnValue) throws ConverterException {
		int ordinal = ((Number)columnValue).intValue();
		if (ordinal < 0) {
			throw new SqlException(
				"Value " + ordinal + " cannot be converted to enum constant");
		}
		if (ordinal >= enumValues.length) {
			throw new SqlException(
				"Value " + ordinal + " cannot be converted to enum constant. Only "
				+ enumValues.length + " constants available"
			);
		}
		return enumValues[ordinal];
	}
}

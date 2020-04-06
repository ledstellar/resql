package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import static ru.resql.util.StringUtils.toDebugString;

public class SimpleEnumConverter  implements Converter<Enum<?>> {
	@SuppressWarnings("rawtypes")
	private final Class<? extends Enum> enumClass;

	SimpleEnumConverter(Class<? extends Enum<?>> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public Enum<?> convert(Object columnValue) throws ConverterException {
		if (columnValue == null) {
			return null;
		}
		String columnValueStr = columnValue.toString();
		try {
			//noinspection unchecked
			return Enum.valueOf(enumClass, columnValueStr);
		} catch (IllegalArgumentException iae) {
			throw new ConverterException(
				columnValueStr,
				"Enum named " + toDebugString(columnValueStr) +  " was not found amount tags of tagged enum "
					+ enumClass.getName()
			);
		}
	}
}

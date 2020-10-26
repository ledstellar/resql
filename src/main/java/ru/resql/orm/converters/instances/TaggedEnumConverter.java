package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.SQLException;
import java.util.HashMap;

import static ru.resql.util.StringUtils.toDebugString;

public class TaggedEnumConverter implements Converter<Enum<?>> {
	private final Converter<Enum<?>> converter;
	private final HashMap<Object, Enum<?>> tagToEnumTable;

	TaggedEnumConverter(Converter<Enum<?>> converter, HashMap<Object, Enum<?>> tagToEnumTable) {
		this.converter = converter;
		this.tagToEnumTable = tagToEnumTable;
	}

	@Override
	public Enum<?> convert(Object columnValue) throws SQLException {
		if (columnValue == null) {
			return null;
		}
		Object tagValue = converter.convert(columnValue);
		Enum<?> value = tagToEnumTable.get(tagValue);
		if (value == null) {
			throw new ConverterException(
				tagValue,
				"Tag " + toDebugString(tagValue) +  " was not found amount tags of tagged enum "
					+ tagToEnumTable.values().iterator().next().getClass().getName()
			);
		}
		return value;
	}
}

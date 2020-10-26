package ru.resql.orm.converters.instances;

import ru.resql.orm.*;
import ru.resql.orm.converters.*;

import java.sql.*;
import java.util.HashMap;

import static java.sql.Types.*;

@SuppressWarnings("unused")
public class EnumConverterFrame implements ConverterFrame<Enum<?>> {
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Converter<Enum<?>> getConverter(ConverterFrames converterFrames, Class<?> destClassType, String destDescription, String columnName, int columnSqlType, String columnTypeName) throws SQLException {
		if (! destClassType.isEnum()) {
			return null;
		}
		if (Tagged.class.isAssignableFrom(destClassType)) {
			// We need to convert to tagged enums. Lets build tag value to enum map
			HashMap<Object, Enum<?>> tagToEnumTable = new HashMap<>();
			// tag class (to find appropriate converter from DB column to tag type)
			Class<?> tagClass = null;
			for (Tagged<?> tagged : (Tagged<?>[])destClassType.getEnumConstants()) {
				tagToEnumTable.put(tagged.getTag(), (Enum<?>)tagged);
				if (tagClass == null) {
					tagClass = tagged.getTagClass();
				}
			}
			// find appropriate converter (converters can be extended by user)
			Converter converter = converterFrames.getConverter(tagClass, destDescription, columnName, columnSqlType, columnTypeName);
			if (converter == null) {
				return null;
			}
			return new TaggedEnumConverter(converter, tagToEnumTable);
		}
		// If not Tagged but still Enum, then we have two mapping options:
		// 1. Integral DB types to Enum.ordinal()
		// 2. Text DB types and ENUM type to Enum.name()
		if (columnSqlType == INTEGER || columnSqlType == BIGINT || columnSqlType == SMALLINT) {
			//noinspection unchecked
			return new IntegralTypeToOrdinalEnumConvertor((Class<Enum<?>>)destClassType);
		}
		if (columnSqlType == VARCHAR || columnSqlType == CHAR) {
			return new TextTypeToNamedEnumConvertor((Class<Enum>)destClassType);
		}
		return null;
	}
}

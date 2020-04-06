package ru.resql.orm.converters.instances;

import ru.resql.orm.*;
import ru.resql.orm.converters.*;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;

@SuppressWarnings("unused")
public class EnumConverterFrame implements ConverterFrame<Enum<?>> {
	@Override
	public Converter<Enum<?>> getConverter(ConverterFrames converterFrames, Class<?> destClassType, String destDescription, String columnName, int columnSqlType, String columnTypeName) throws SQLException {
		if (destClassType.isEnum()) {
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
				Converter<?> converter = converterFrames.getConverter(tagClass, destDescription, columnName, columnSqlType, columnTypeName);
				if (converter == null) {
					throw new ClassMappingException(destClassType,
						"Cannot find convertor from DB column type " + columnSqlType
						+ " to tagged enum class " + destClassType.getName()
					);
				}
				return new TaggedEnumConverter(converter, tagToEnumTable);
			} else {
				// We need to convert to untagged enums. There can be only strict name to name mapping
				// So as enum name is of String class then we need converter from DB column type to String
				Converter<?> converter = converterFrames.getConverter(String.class, destDescription, columnName, columnSqlType, columnTypeName);
				if (converter == null) {
					throw new ClassMappingException(destClassType,
						"Cannot find convertor from DB column type " + columnSqlType
							+ " to simple enum class " + destClassType.getName()
					);
				}
				//noinspection unchecked
				return new SimpleEnumConverter((Class<? extends Enum<?>>)destClassType);
			}
		}
		return null;
	}
}

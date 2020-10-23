package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.Types;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ArrayOfEnumsConverterFrame implements ConverterFrame<Enum<?>[]> {
	@SuppressWarnings("unchecked") @Override
	public Converter<Enum<?>[]> getConverter(ConverterFrames converterFrames, Class<?> destClass, String destinationDescription, String columnName, int columnSqlType,
	 String columnTypeName) {
		if (destClass.isArray() && (
			columnSqlType == Types.ARRAY ||
			columnSqlType == Types.OTHER // TODO: driver bug? If enum is NOT from public schema then it marked as type OTHER
		)) {
			Class<?> elementType = destClass.getComponentType();
			if (elementType.isEnum()) {
				@SuppressWarnings("rawtypes")
				Class<? extends Enum> enumClass = (Class<? extends Enum>)elementType;
				return columnValue -> {
					// Custom types (and enums) are still not fully supported at client side (see https://github.com/pgjdbc/pgjdbc/pull/1378)
					// so we have to decode them all by ourselves
					ArrayList<String> values = parseArrayOfEnumValues(columnValue.toString());
					Enum<?>[] resultArray = (Enum<?>[])java.lang.reflect.Array.newInstance(elementType, values.size());
					int index = 0;
					for (String enumStr : values) {
						resultArray[index++] = Enum.valueOf(enumClass, enumStr);
					}
					return resultArray;
				};
			}
		}
		return null;
	}

	ArrayList<String> parseArrayOfEnumValues(String value) {
		boolean inCommas = false;
		ArrayList<String> values = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < value.length(); ++ i) {
			char ch = value.charAt(i);
			if (inCommas) {
				if (ch == '"') {
					inCommas = false;
				} else {
					builder.append(ch);
				}
			} else {
				if (ch == '"') {
					inCommas = true;
				} else if (ch == ',') {
					values.add(builder.toString());
					builder.setLength(0);
				} else if (ch != '{' && ch != '}') {
					builder.append(ch);
				}
			}
		}
		values.add(builder.toString());
		return values;
	}

}

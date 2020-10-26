package ru.resql.orm.converters.instances;

import ru.resql.SqlException;
import ru.resql.orm.converters.*;

import java.util.ArrayList;

public class UndecodedArrayOfEnumConverter extends ConverterImpl<Object> {
	Class<?> destEnumClass;

	UndecodedArrayOfEnumConverter(String columnDescription, String fieldDescription, Class<?> destEnumClass) {
		super(columnDescription, fieldDescription);
		this.destEnumClass = destEnumClass;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Object convert(Object columnValue) throws ConverterException {
		// Custom types (and enums) are still not fully supported at client side
		// (see https://github.com/pgjdbc/pgjdbc/pull/1378) so we have to decode them all by ourselves
		ArrayList<String> values = parseArrayOfEnumValues(columnValue.toString());
		Enum<?>[] resultArray = (Enum<?>[])java.lang.reflect.Array.newInstance(destEnumClass, values.size());
		int index = 0;
		for (String enumStr : values) {
			try {
				resultArray[index++] = Enum.valueOf((Class<? extends Enum>) destEnumClass, enumStr);
			} catch (IllegalArgumentException iae) {
				throw new SqlException(
					"Cannot supply Enum constant of type " + destEnumClass.getName()
					+ " to  PostgreSQL ENUM value '" + enumStr + "'");
			}
		}
		return resultArray;
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

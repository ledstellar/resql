package ru.resql.util;

public class StringUtils {
	private StringUtils() {}

	public static String toDebugString(Object value) {
		if (value == null) {
			return "NULL";
		}
		if (value instanceof Number) {
			return value.toString();
		}
		return '"' + value.toString() + '"';
	}
}

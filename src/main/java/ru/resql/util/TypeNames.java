package ru.resql.util;

import org.slf4j.*;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.*;

public class TypeNames {
	private static Logger log = LoggerFactory.getLogger(TypeNames.class);

	private static Map<Integer, String> typeMap = getAllJdbcTypeNames();

	public static String getName(int type) {
		return typeMap.get(type);
	}

	public static Map<Integer, String> getAllJdbcTypeNames() {
		Map<Integer, String> result = new HashMap<>();
		for (Field field : Types.class.getFields()) {
			try {
				result.put((Integer) field.get(null), field.getName());
			} catch (IllegalAccessException iae) {
				log.warn("Cannot get type name of " + field.getName(), iae);
			}
		}
		return result;
	}
}

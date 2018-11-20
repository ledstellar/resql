package io.resql.orm;

import io.resql.SqlException;
import io.resql.orm.converters.Converter;
import io.resql.util.TypeNames;
import org.reflections.Reflections;
import org.slf4j.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * ORM class access implementation.
 * Its ok for ORM class to have more class fields than supplied by query. However it's not ok when supplied query contains more
 * data columns than ORM class fields. If constructor accessor is used then constructor with exact parameters isNamesMatch exists.
 *
 * @param <T> ORM class type
 */
public abstract class Accessor<T> {
	private static final Logger log = LoggerFactory.getLogger(Accessor.class);

	boolean isConvertorAvailable(String columnTypeName, Class<?> fieldClass) {
		return findConvertor(columnTypeName, fieldClass) != null;
	}

	static HashMap<String, HashMap<Class<?>, Converter>> converters = new HashMap<>();

	static {
		loadConverters();
	}

	private static void loadConverters() {
		Reflections reflections = new Reflections("io.resql.orm.converters");
		for(Class<? extends Converter> converterClass : reflections.getSubTypesOf(Converter.class)) {
			try {
				Converter converter = converterClass.getConstructor().newInstance();
				String dbColumnType = converter.getDbColumnType();
				var converterMap = converters.computeIfAbsent(dbColumnType, key -> new HashMap<>());
				Class<?> fieldType = converter.getFieldType();
				if (converterMap.containsKey(fieldType)) {
					log.error("Converters {} and {} have the same convertion types", converter, converterMap.get(fieldType));
				} else {
					converterMap.put(fieldType, converter);
				}
			} catch (Exception e) {
				log.error("Cannot instantiate converter of class " + converterClass, e);
			}
		}

	}

	/**
	 * Create new ORM class access implementation. Use either factory or targetClass param
	 *
	 * @param resultSetColumnTypes column name to SQL column type map
	 * @param factory              factory for ORM class. When set then accessor will use direct access to class' members.
	 *                             TODO: describe member resolution rules in details
	 * @param targetClass          ORM class. When set then access will scan ORM class for appropriate constructor
	 */
	static <T> Accessor<T> newInstance(LinkedHashMap<String, String> resultSetColumnTypes, Supplier<T> factory, Class<T> targetClass,
	                                   ConverterFactory converterFactory) throws
		SQLException {
		if (factory != null) {
			return new FieldDirectAccessor<>(resultSetColumnTypes, factory);
		} else {
			return new ConstructorAccessor<>(resultSetColumnTypes, targetClass, converterFactory);
		}
	}

	Converter findConvertor(String columnType, Class<?> fieldClass) {
		log.debug("Scan for converter from {} to {}", columnType, fieldClass.getName());
		var converterMap = converters.get(columnType);
		if (converterMap == null) {
			throw new RuntimeException("No converters defined for DB column type " + columnType); // TODO: make specific exception
		}
		var converter = converterMap.get(fieldClass);
		if (converter == null) {
			// TODO: make specific exception (as above) and show more details in description (already defined class types for example)
			throw new RuntimeException("Some converters defined for DB column type " + columnType + ", but not for class " + fieldClass);
		}
		return converter;
	}

	String getColumnDescription(String columnName, String sqlColumnType) throws SQLException {
		return columnName + ' ' + sqlColumnType;    // TODO: implement more accurately
	}

	static boolean isNamesMatch(String columnName, String fieldName) {
		// TODO: implement more smart algorithm and make it customizable
		return columnName.equals(fieldName);
	}

	/**
	 * Create or get new instance of ORM class and init it with resultset's current record. ORM class instance can be supplied by ORM class supplier
	 * or created by accessor using appropriate class constructor
	 *
	 * @param resultSet opened resultset to init ORM class instance
	 * @return initialized ORM class instance
	 */
	public abstract T get(ResultSet resultSet);
}

package ru.resql.orm.converters.instances;

import lombok.extern.slf4j.Slf4j;
import ru.resql.orm.Tagged;
import ru.resql.orm.converters.*;

import java.sql.*;
import java.util.*;

import static java.sql.Types.*;

@SuppressWarnings("unused")
@Slf4j
public class ArrayConverterFrame implements ConverterFrame<Object> {
	private static class PgType {
		final int sqlType;
		final String className;

		PgType(int sqlType, String className) {
			this.sqlType = sqlType;
			this.className = className;
		}
	}

	HashMap<String, PgType> pgTypes = new HashMap<>();

	public ArrayConverterFrame() {
		// see org.postgresql.jdbc.TypeInfoCache.types and org.postgresql.jdbc.PgArray.elementOidToClass(int oid)
		pgTypes.put("int2", new PgType(SMALLINT, "java.lang.Integer"));
		pgTypes.put("int4", new PgType(INTEGER, "java.lang.Integer"));
		pgTypes.put("oid", new PgType(BIGINT, "java.lang.Long"));
		pgTypes.put("int8", new PgType(BIGINT, "java.lang.Long"));
		pgTypes.put("money", new PgType(DOUBLE, "java.lang.Double"));
		pgTypes.put("numeric", new PgType(NUMERIC, "java.math.BigDecimal"));
		pgTypes.put("float4", new PgType(REAL, "java.lang.Float"));
		pgTypes.put("float8", new PgType(DOUBLE, "java.lang.Double"));
		pgTypes.put("char", new PgType(CHAR, "java.lang.String"));
		pgTypes.put("bpchar", new PgType(CHAR, "java.lang.String"));
		pgTypes.put("varchar", new PgType(VARCHAR, "java.lang.String"));
		pgTypes.put("text", new PgType(VARCHAR, "java.lang.String"));
		pgTypes.put("name", new PgType(VARCHAR, "java.lang.String"));
		pgTypes.put("bytea", new PgType(BINARY, "[B"));
		pgTypes.put("bool", new PgType(BIT, "java.lang.Boolean"));
		pgTypes.put("bit", new PgType(BIT, "java.lang.Boolean"));
		pgTypes.put("date", new PgType(DATE, "java.sql.Date"));
		pgTypes.put("time", new PgType(TIME, "java.sql.Time"));
		pgTypes.put("timetz", new PgType(TIME, "java.sql.Time"));
		pgTypes.put("timestamp", new PgType(TIMESTAMP, "java.sql.Timestamp"));
		pgTypes.put("timestamptz", new PgType(TIMESTAMP, "java.sql.Timestamp"));
		pgTypes.put("refcursor", new PgType(REF_CURSOR, "java.sql.ResultSet"));
		pgTypes.put("json", new PgType(OTHER, "org.postgresql.util.PGobject"));
		pgTypes.put("point", new PgType(OTHER, "org.postgresql.geometric.PGpoint"));
	}

	@Override
	public Converter<Object> getConverter(ConverterFrames converterFrames, Class<?> destClass, String destinationDescription, String columnDescription, int columnSqlType, String columnTypeName) throws SQLException {
		if (!destClass.isArray() || !columnTypeName.startsWith("_")) {
			return null;
		}
		Class<?> componentType = destClass.getComponentType();
		if (columnSqlType != Types.ARRAY
			// TODO: is that a driver bug? If ENUM is NOT from the 'public' schema
			// then its array marked not as ARRAY type but as OTHER type
			&& (columnSqlType != Types.OTHER || componentType.isEnum())
		) {
			return null;
		}
		// PostgreSQL JDBC driver marks array types with underscore at the beginning of db type
		String inArrayColumnDbType = columnTypeName.substring(1);
		PgType pgType = pgTypes.get(inArrayColumnDbType);
		if (pgType == null) {
			// There can be one special case: we might get here array of ENUM (PostgreSQL custom type)
			// encoded into string (driver does not decode it). We cannot detect it directly because we are not
			// checking DB type metadata (yet), but we can detect it indirectly by destination type. So ...
			if (componentType.isEnum() && // ... if destination type is Enum
				// and not marked with interface Tagged
				// (i. e. conversion PostgreSQL ENUM[] -> decode encoded String -> array of java Enums by names) ...
				!componentType.isAssignableFrom(Tagged.class)
			) {
				// ...then we can provide special decoder. This decoder will find and use String to Enum mapper at the end
				return new UndecodedArrayOfEnumConverter(columnDescription, destinationDescription, componentType);
			}
			return null;
		}
		Converter<Object> converter = converterFrames.getConverter(
			componentType, destinationDescription, columnDescription, pgType.sqlType, inArrayColumnDbType
		);
		// or at least tagged with String
// 					|| ((Tagged<?>)componentType.getEnumConstants()[0]).getTag().getClass().isAssignableFrom(String.class)
		if (converter instanceof ToPrimitiveConverter) {
			// Primitive arrays need special treatments as we can't iterate primitive arrays
			// the same way we iterate Object array. Also conversion from Object to primitive type
			// has to be of two steps (see below)
			// TODO: need to improve logging of mapping (i.e. converters of arrays of custom types)
			if (int.class.isAssignableFrom(componentType)) {
				return new ArrayOfPrimitiveIntConverter(
					columnDescription, destinationDescription, converter
				);
			}
			if (long.class.isAssignableFrom(componentType)) {
				return new ArrayOfPrimitiveLongConverter(
					columnDescription, destinationDescription, converter
				);
			}
			if (boolean.class.isAssignableFrom(componentType)) {
				return new ArrayOfPrimitiveBooleanConverter(
					columnDescription, destinationDescription, converter
				);
			}
			if (double.class.isAssignableFrom(componentType)) {
				return new ArrayOfPrimitiveDoubleConverter(
					columnDescription, destinationDescription, converter
				);
			}
			if (float.class.isAssignableFrom(componentType)) {
				return new ArrayOfPrimitiveFloatConverter(
					columnDescription, destinationDescription, converter
				);
			}
		}
		// TaggedEnumConverter is also ends here
		return new ArrayOfCustomTypeConverter(
			columnDescription, destinationDescription, converter, componentType
		);
	}
}

package ru.resql.orm.vendor.postgresql;

import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.*;
import java.util.*;

import static ru.resql.orm.vendor.postgresql.PgSqlType.*;

// see org.postgresql.jdbc.TypeInfoCache.types
public enum PgType {
	int2("java.lang.Integer", Short.class, SMALLINT, true),
	int4("java.lang.Integer", Integer.class, INTEGER, true),
	oid("java.lang.Long", Long.class, OID, false),
	int8("java.lang.Long", Long.class, BIGINT, true),
	money("java.lang.Double", BigDecimal.class, MONEY, false),
	numeric("java.math.BigDecimal", BigDecimal.class, NUMERIC, true),
	float4("java.lang.Float", Float.class, REAL, true),
	float8("java.lang.Double", Double.class, DOUBLE_PRECITION, true),
	Char("java.lang.String", String.class, CHAR, false),
	bpchar("java.lang.String", String.class, CHAR, false), // Blank padded char. TODO: test its usage
	varchar("java.lang.String", String.class, VARCHAR, false),
	text("java.lang.String", String.class, TEXT, true),
	name("java.lang.String", String.class, NAME, false),
	bytea("[B", byte[].class, BYTEA, true),
	bool("java.lang.Boolean", Boolean.class, BOOLEAN, true),
	bit("java.lang.Boolean", Boolean.class, BIT, false), // TODO: Why Boolean? test it. Probably bug in TypeInfoCache.types
	date("java.sql.Date", LocalDate.class, DATE, true),
	time("java.sql.Time", LocalTime.class, TIME_WITHOUT_TIMEZONE, false),
	timetz("java.sql.Time", LocalTime.class, TIME_WITH_TIMEZONE, true),
	timestamp("java.sql.Timestamp", LocalDateTime.class, TIMESTAMP_WITHOUT_TIMEZONE, false),
	timestamptz("java.sql.Timestamp", LocalDateTime.class, TIMESTAMP_WITH_TIMEZONE, true),
	// TODO: add interval and hstore types
	refcursor("java.sql.ResultSet", ResultSet.class, RESULTSET, true),
	json("org.postgresql.util.PGobject", String.class, JSON, false);
	// TODO: implement geometric, network, interval and other types
//	point("org.postgresql.geometric.PGpoint", Object.class, POINT);

	public final String sqlTypeName;
	public Class<?> driverClass;
	public final Class<?> paramClass;
	public final PgSqlType paramModifier;
	private final boolean isDefaultParamConvertion;

	PgType(String driversJavaClassName, Class<?> paramTypeClass, PgSqlType modifier, boolean isDefaultParamConvertion) {
		sqlTypeName = name().toLowerCase();
		try {
			driverClass = Class.forName(driversJavaClassName);
		} catch (ClassNotFoundException cnfe) {
			LoggerFactory.getLogger(PgType.class).warn(
				"Cannot instantiate PostgreSQL JDBC DTO class " + driversJavaClassName + ". No pgjdbc library on classpath?",
				cnfe
			);
		}
		paramClass = paramTypeClass;
		paramModifier = modifier;
		this.isDefaultParamConvertion = isDefaultParamConvertion;
	}

	public static final Map<String, Class<?>> dbTypeToJavaClasses;
	public static final Map<Class<?>, String> paramJavaClassToDefaultDbType;

	static {
		HashMap<String, Class<?>> fromDbMap = new HashMap<>();
		for (PgType pgType : values()) {
			fromDbMap.put(pgType.sqlTypeName, pgType.driverClass);
		}
		dbTypeToJavaClasses = Collections.unmodifiableMap(fromDbMap);
	}

	static {
		HashMap<Class<?>, String> toDbMap = new HashMap<>();
		for (PgType pgType : values()) {
			if (pgType.isDefaultParamConvertion) {
				if (toDbMap.containsKey(pgType.paramClass)) {
					throw new RuntimeException(
						"Internal error: multiple default parameter conversions from java class " + pgType.paramClass.getName()
					);
				}
				toDbMap.put(pgType.paramClass, pgType.sqlTypeName);
			}
		}
		paramJavaClassToDefaultDbType = Collections.unmodifiableMap(toDbMap);
	}
}

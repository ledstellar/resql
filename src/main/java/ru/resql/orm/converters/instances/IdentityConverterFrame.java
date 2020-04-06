package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;
import ru.resql.orm.vendor.postgresql.PgType;

@SuppressWarnings("unused")
public class IdentityConverterFrame implements ConverterFrame<Object> {
	@Override
	public Converter<Object> getConverter(ConverterFrames converterFrames, Class<?> destClassType, String destDescription, String columnName, int columnSqlType, String columnTypeName) {
		Class<?> javaClass = PgType.dbTypeToJavaClasses.get(columnTypeName);
		return (javaClass != null && destClassType.isAssignableFrom(javaClass))
			? new IdentityConverter(columnName, destDescription)
			: null;
	}
}

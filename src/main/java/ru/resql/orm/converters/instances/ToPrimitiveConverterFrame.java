package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;
import ru.resql.orm.vendor.postgresql.PgType;
import ru.resql.util.PrimitiveUtil;

@SuppressWarnings("unused")
public class ToPrimitiveConverterFrame implements ConverterFrame<Object> {
	@Override
	public Converter<Object> getConverter(
		ConverterFrames converterFrames, Class<?> result, String destinationDescription,
		String columnName, int columnSqlType, String columnTypeName
	) {
		Class<?> javaClass = PgType.dbTypeToJavaClasses.get(columnTypeName);
		if (javaClass == null || !result.equals(PrimitiveUtil.objectToPrimitives.get(javaClass))) {
			return null;
		}
		return new ToPrimitiveConverter(columnName, destinationDescription);
	}
}

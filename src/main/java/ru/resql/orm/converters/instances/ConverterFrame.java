package ru.resql.orm.converters.instances;

import ru.resql.orm.converters.*;

import java.sql.SQLException;

public interface ConverterFrame<ResultType> {
	/**
	 * Supply data converter for given data source and destination if possible
	 * @param converterFrames existing data converter suppliers (can be used to build nested conversions)
	 * @param result class of conversion destination
	 * @param destinationDescription description of conversion destination element. Can describe field in ORM class, stream element etc.
	 * @param columnName column name of query result used as data source. Intended to describe data source
	 * @param columnSqlType JDBC standard sql type of query result column
	 * @param columnTypeName query result column database specific type
	 * @return converter instance if appropriate or <code>null</code> if no converters can be supplied for this case
	 * @throws SQLException if database errors occurs
	 */
	Converter<ResultType> getConverter(ConverterFrames converterFrames, Class<?> result, String destinationDescription, String columnName, int columnSqlType, String columnTypeName) throws SQLException;
}

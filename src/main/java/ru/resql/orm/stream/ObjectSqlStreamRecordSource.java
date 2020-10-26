package ru.resql.orm.stream;

import ru.resql.*;
import ru.resql.orm.converters.*;

import java.sql.*;
import java.util.function.Consumer;

public class ObjectSqlStreamRecordSource<ElementType> extends ObjectSqlStreamChain<ElementType>
	implements SqlDataSourceKeeper<ElementType>
{
	final Class<ElementType> elementClass;
	Converter<ElementType> converter;
	SelectWithParamSqlDataSource sqlDataSource;
	final ConverterFrames converterFrames;

	public ObjectSqlStreamRecordSource(Class<ElementType> elementClass, SelectWithParamSqlDataSource sqlDataSource, ConverterFrames converterFrames) {
		super(null);
		this.elementClass = elementClass;
		this.sqlDataSource = sqlDataSource;
		this.converterFrames = converterFrames;
	}

	@Override
	public SelectWithParamSqlDataSource getSqlDataSource() {
		return sqlDataSource;
	}

	@Override boolean tryAdvance(Consumer<? super ElementType> action) throws SqlException {
		if (sqlDataSource.runQueryIfNotYet()) {
			setupResultSetMapper();
		}
		try {
			if (sqlDataSource.resultSet.next()) {
				action.accept(converter.convert(sqlDataSource.resultSet.getObject(1)));
				return true;
			} else {
				return false;
			}
		} catch (SQLException sqle) {
			throw new SqlException("Can't get next element", sqle);
		}
	}

	protected void setupResultSetMapper() {
		try {
			ResultSetMetaData resultSetMetaData = sqlDataSource.resultSet.getMetaData();
			if (resultSetMetaData.getColumnCount() != 1) {
				throw new SqlException(
					"SQL query of object stream must has exactly one result column but this query returns "
					+ resultSetMetaData.getColumnCount() + " columns. Change query or use ORM stream instead"
				);
			}
			converter = converterFrames.getConverter(
				elementClass, "<StreamElement>", resultSetMetaData.getColumnName(1), resultSetMetaData.getColumnType(1),
				 resultSetMetaData.getColumnTypeName(1)
			);
			if (converter == null) {
				throw new SqlException(
					"ResultSet (" + resultSetMetaData.getColumnTypeName(1) + " " + resultSetMetaData.getColumnName(1)
					+ ") cannot be converted to object stream of type " + describeType(elementClass)
				);
			}
		} catch (SQLException sqlException) {
			throw new SqlException("Error setting up recordset to object converter", sqlException);
		}
	}

	private String describeType(Class<?> clazz) {
		if (clazz.isArray()) {
			return clazz.getComponentType().getName() + "[]";
		}
		return elementClass.getName();
	}

	@Override
	public void close() {
		sqlDataSource.close();
	}
}

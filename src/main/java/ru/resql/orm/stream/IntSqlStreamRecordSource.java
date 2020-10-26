package ru.resql.orm.stream;

import ru.resql.SqlException;
import ru.resql.orm.converters.*;

import java.sql.*;
import java.util.function.IntConsumer;

public class IntSqlStreamRecordSource extends IntSqlStreamChain {
	final SelectWithParamSqlDataSource sqlDataSource;
	Converter<Integer> converter;
	private final ConverterFrames converterFrames;

	public IntSqlStreamRecordSource(SelectWithParamSqlDataSource sqlDataSource, ConverterFrames converterFrames) {
		this.sqlDataSource = sqlDataSource;
		this.converterFrames = converterFrames;
	}

	@Override
	public void close() {
		sqlDataSource.close();
	}

	@Override
	boolean tryAdvance(IntConsumer action) throws SqlException {
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
				int.class, "<StreamElement>", resultSetMetaData.getColumnName(1), resultSetMetaData.getColumnType(1),
				resultSetMetaData.getColumnTypeName(1)
			);
			if (converter == null) {
				throw new SqlException(
					"ResultSet (" + resultSetMetaData.getColumnTypeName(1) + " " + resultSetMetaData.getColumnName(1)
						+ ") cannot be converted to object stream of type int"
				);
			}
		} catch (SQLException sqlException) {
			throw new SqlException("Error setting up recordset to object converter", sqlException);
		}
	}
}

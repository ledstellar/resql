package ru.resql.orm.converters.instances;

import java.sql.*;

public class ColumnDescriptor {
	public final int columnSqlType;
	public final String columnDbTypeName;
	public final String columnClassName;

	public ColumnDescriptor(int columnSqlType, String columnDbTypeName, String columnClassName) {
		this.columnSqlType = columnSqlType;
		this.columnDbTypeName = columnDbTypeName;
		this.columnClassName = columnClassName;
	}

	public ColumnDescriptor(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		this(
			resultSetMetaData.getColumnType(columnIndex),
			resultSetMetaData.getColumnTypeName(columnIndex),
			resultSetMetaData.getColumnClassName(columnIndex)
		);
	}
}

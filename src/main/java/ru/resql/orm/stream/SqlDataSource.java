package ru.resql.orm.stream;

import org.slf4j.Logger;
import ru.resql.ConnectionWrapper;

import java.sql.*;

public abstract class SqlDataSource {
	protected final Logger log;
	protected final ConnectionWrapper connectionSource;

	public SqlDataSource(ConnectionWrapper connectionSource, Logger log) {
		this.log = log;
		this.connectionSource = connectionSource;
	}

	public abstract Object getAccessorKey();

	public Logger getLogger() {
		return log;
	}

	public abstract ResultSetMetaData getResultSetMetaData() throws SQLException;

	public void close() {
		connectionSource.close();
	}

	public abstract boolean isOptionalFields();
}

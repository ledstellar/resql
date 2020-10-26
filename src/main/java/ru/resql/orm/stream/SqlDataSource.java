package ru.resql.orm.stream;

import org.slf4j.Logger;
import ru.resql.ConnectionWrapper;

import java.sql.*;

public abstract class SqlDataSource {
	protected final Logger log;
	protected final ConnectionWrapper connectionSource;
	protected ResultSet resultSet;

	public SqlDataSource(ConnectionWrapper connectionSource, Logger log) {
		this.log = log;
		this.connectionSource = connectionSource;
	}

	public abstract Object getAccessorKey();

	public Logger getLogger() {
		return log;
	}

	public ResultSetMetaData getResultSetMetaData() throws SQLException {
		return resultSet.getMetaData();
	}

	public void close() {
		connectionSource.close();
	}
}

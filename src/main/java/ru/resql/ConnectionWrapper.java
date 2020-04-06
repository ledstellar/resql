package ru.resql;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class ConnectionWrapper {
	protected DbManager dbManager;
	protected Connection connection;
	protected boolean isReadOnly;
	protected boolean isInitiated;

	public ConnectionWrapper(DbManager dbManager, boolean isReadOnly) {
		this.dbManager = dbManager;
		this.isReadOnly = isReadOnly;
	}

	public Connection getConnection() throws SqlException {
		if (connection != null) {
			// connection already opened. Reuse it
			return connection;
		}
		isInitiated = true;
		return (connection = dbManager.getConnection(!isReadOnly));
	}

	public void close() {
		internalCloseConnection();
	}

	public String getConnectionSourceDescription() {
		return dbManager.getConnectionSourceDescription();
	}

	private void internalCloseConnection() {
		if (!isInitiated) {
			return;
		}
		if (connection == null)  {
			throw new RuntimeException("Connection is not opened or already closed");
		}
		try {
			connection.close();
		} catch (SQLException sqlException) {
			log.error("Error closing connection", sqlException);
		} finally {
			connection = null;
		}
	}

	public boolean initiateIfNeed() {
		if (!isInitiated) {
			connection = dbManager.getConnection(!isReadOnly);
			isInitiated = true;
			return true;
		}
		return false;
	}
}

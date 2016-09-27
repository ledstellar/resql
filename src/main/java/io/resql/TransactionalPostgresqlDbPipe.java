package io.resql;

import java.sql.*;

public class TransactionalPostgresqlDbPipe extends PostgresqlDbPipe {
	protected Connection connection;

	TransactionalPostgresqlDbPipe( PostgresqlDbManager postgresqlDbManager ) throws SQLException {
		super( postgresqlDbManager );
		connection = postgresqlDbManager.getConnection();
		connection.setAutoCommit( false );
	}

	@Override
	protected Connection getConnection() throws SQLException {
		return super.getConnection();
	}
}

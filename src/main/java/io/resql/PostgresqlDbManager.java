package io.resql;

import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.*;

public class PostgresqlDbManager extends DbManager {
	PostgresqlDbManager( DataSource dataSource ) {
		super( dataSource );
	}

	@Override
	public PostgresqlDbPipe getPipe(Logger log) {
		return new PostgresqlDbPipe( this, log );
	}

	Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}

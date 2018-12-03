package io.resql;

import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgresqlDbManager extends DbManager {
	PostgresqlDbManager( DataSource dataSource ) {
		super( dataSource );
	}

	@Override
	public PostgresqlDbPipe getPipe(Logger log) {
		return new PostgresqlDbPipe(dataSource, accessorFactory, log);
	}
}

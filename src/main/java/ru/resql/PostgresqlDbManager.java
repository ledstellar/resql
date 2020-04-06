package ru.resql;

import org.slf4j.Logger;

import javax.sql.DataSource;

public class PostgresqlDbManager extends DbManager {
	public PostgresqlDbManager(DataSource dataSource, String dataSourceDescription) {
		super(dataSource, dataSourceDescription);
	}

	@Override
	public PostgresqlDbPipe getPipe(Logger log) {
		return new PostgresqlDbPipe(this, log);
	}
}

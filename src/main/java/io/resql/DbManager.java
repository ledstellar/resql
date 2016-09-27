package io.resql;

import org.slf4j.Logger;

import javax.sql.DataSource;

/**
 * Connection(s) tied to a particular database.
 */
public abstract class DbManager {
	DataSource dataSource;

	DbManager( DataSource dataSource ) {
		this.dataSource = dataSource;
	}

	public abstract DbPipe getPipe( Logger log );
}

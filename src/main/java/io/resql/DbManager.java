package io.resql;

import io.resql.orm.AccessorFactory;
import org.slf4j.Logger;

import javax.sql.DataSource;

/**
 * Connection(s) tied to a particular database.
 */
public abstract class DbManager {
	DataSource dataSource;
	AccessorFactory accessorFactory;

	DbManager( DataSource dataSource ) {
		this.dataSource = dataSource;
		accessorFactory = new AccessorFactory();
	}

	public abstract DbPipe getPipe( Logger log );
}

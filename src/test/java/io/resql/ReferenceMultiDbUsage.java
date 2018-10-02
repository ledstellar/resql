package io.resql;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.*;

import javax.sql.DataSource;

/**
 * Reference (pure Java) DbManager usage example.
 */
public class ReferenceMultiDbUsage {
	static final Logger log = LoggerFactory.getLogger(ReferenceMultiDbUsage.class);
	static final DataSource dataSource = new HikariDataSource();
	static final PostgresqlDbManager dbManager = new PostgresqlDbManager(dataSource);
	static final DbPipe dbPipe = dbManager.getPipe(log);

	public ReferenceMultiDbUsage() {}
}
package io.resql;

import com.zaxxer.hikari.HikariDataSource;
import io.resql.extractors.IntResult;
import org.junit.*;
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

	@Ignore( "Next step" )
	@Test
	public void testSingleExtractor() {
		int result = dbPipe.query( IntResult.single, "SELECT 1 FROM dual" );
	}
}
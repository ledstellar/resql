package io.resql;

import com.zaxxer.hikari.HikariDataSource;
import io.resql.extractors.IntResult;
import org.junit.*;
import org.slf4j.*;

public class PureJavaSingleDbTest {
	private static final Logger log = LoggerFactory.getLogger( PureJavaSingleDbTest.class );
	private static HikariDataSource ds = new HikariDataSource();
	static {
		ds.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
		ds.setUsername("test");
		ds.setPassword("testpwd");
	}
	private static PostgresqlDbManager dbManager = new PostgresqlDbManager( ds );
	private static final DbPipe pipe = dbManager.getPipe( log );

	public PureJavaSingleDbTest() {}

	@Test public void simpleTest() {
		Assert.assertEquals( (Integer)1, pipe.query( IntResult.single, "SELECT 1" ) );
	}
}

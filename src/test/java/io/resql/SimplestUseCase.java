package io.resql;

import com.zaxxer.hikari.*;
import org.junit.*;
import org.slf4j.*;

import java.lang.reflect.Constructor;
import java.util.function.*;

/**
 * Typical use case of reSql without any DI or something
 */
public class SimplestUseCase {
	private static final Logger log = LoggerFactory.getLogger( SimplestUseCase.class );

	public SimplestUseCase() {}

	@Test
	public void testSimplestUseCase() {
		HikariConfig config = new HikariConfig("/simple_use_case_hikari.properties" );
		HikariDataSource dataSource = new HikariDataSource( config );
		PostgresqlDbManager dbManager = new PostgresqlDbManager( dataSource );
		PostgresqlDbPipe pipe = dbManager.getPipe( log );
		Assert.assertEquals(
			"No records must be processed by DDL queries", 0,
			pipe.execute("CREATE TABLE IF NOT EXISTS users(id SERIAL, name TEXT)" )
		);
		User user = pipe.select(As::single, User.class,"SELECT * FROM users WHERE id=?", 1);
		Assert.assertEquals("Users should be the same",user,new  User(1, "User Name"));
		Assert.assertEquals(
			"No records must be processed by DDL queries", 0,
			pipe.execute("DROP TABLE test_table" )
		);
	}

	@Test
	public void testShortestUseCase() {
		new PostgresqlDbManager(
			new HikariDataSource(
				new HikariConfig("/simple_use_case_hikari.properties" )
			)
		).getPipe( log ).execute( "CREATE TABLE IF NOT EXISTS test_table( id SERIAL, text_data TEXT )" );
	}
}

package io.resql;

import com.zaxxer.hikari.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Typical use case of reSql without any DI or something
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimplestUseCase {
	private static final Logger log = LoggerFactory.getLogger( SimplestUseCase.class );

	private PostgresqlDbPipe pipe;

	@BeforeAll
	void initTable() {
		assertEquals(
			0, pipe.execute("CREATE TABLE IF NOT EXISTS users(id SERIAL, name TEXT)"),
			"No records must be processed by DDL queries"
		);
	}

	SimplestUseCase() {
		HikariConfig config = new HikariConfig("/simple_use_case_hikari.properties" );
		HikariDataSource dataSource = new HikariDataSource( config );
		PostgresqlDbManager dbManager = new PostgresqlDbManager( dataSource );
		pipe = dbManager.getPipe( log );
	}

	@Test
	void testWrongFieldOrder() {
		String sql = "SELECT name, id FROM users WHERE id=?";
		SqlException sqle = assertThrows(
			SqlException.class, () -> { pipe.select(As::single, User.class, sql, 1); },
			"Wrong field sequence check"
		);
		assertEquals("Error executing " + sql, sqle.getMessage());
		Throwable cause = sqle.getCause();
		assertEquals(
			"Can't find appropriate constructor among:\n" +
				"\tio.resqlUserio.resql.User()\n" +
				"\tio.resqlUserio.resql.User(int, java.lang.String)\n" +
				"for result set fields:\n" +
				"\tname VARCHAR, id INTEGER",
			cause.getMessage()
		);
	}

	@Test
	void testSelectSingle() {
		User user = pipe.select(As::single, User.class, "SELECT id, name FROM users WHERE id=?", 1 );
 		assertEquals(user, new User(1, "User Name"),"Users should be the same");
	}

	@AfterAll
	void dropTable() {
		assertEquals(
			0, pipe.execute("DROP TABLE users" ),
			"No records must be processed by DDL queries"
		);
	}
}

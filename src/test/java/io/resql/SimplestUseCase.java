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
		fillTableUp();
	}

	SimplestUseCase() {
		HikariConfig config = new HikariConfig("/simple_use_case_hikari.properties" );
		HikariDataSource dataSource = new HikariDataSource( config );
		PostgresqlDbManager dbManager = new PostgresqlDbManager( dataSource );
		pipe = dbManager.getPipe(log);
	}

	private void fillTableUp() {
		pipe.batch(
			Query::insert,
			batch -> {
				User user = new User();
				for (int id = 1; id < 1000; ++ id) {
					user.id = id;
					user.name = "Имя пользователя " + id;
					batch.add(user);
				}
			}
		);
	}

	@AfterAll
	void dropTable() {
		assertEquals(
			0, pipe.execute("DROP TABLE users" ),
			"No records must be processed by DDL queries"
		);
	}
}

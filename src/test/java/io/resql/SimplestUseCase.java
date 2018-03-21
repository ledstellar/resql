package io.resql;

import com.zaxxer.hikari.*;
import org.slf4j.*;

/**
 * Typical use case of reSql without any DI or something
 */
class SimplestUseCase {
	private static final Logger log = LoggerFactory.getLogger( SimplestUseCase.class );
	private final PostgresqlDbManager dbManager;
//	private final PostgresqlDbPipe dbPipe;

	private SimplestUseCase() {
		HikariConfig config = new HikariConfig("/simpleusecase_hikari.properties" );
		HikariDataSource dataSource = new HikariDataSource( config );
		dbManager = new PostgresqlDbManager( dataSource );
		PostgresqlDbPipe pipe = dbManager.getPipe( log );
		Object obj = pipe.select(As::single, Object::new,"SELECT 1");
	}

	private void compareWatches() {
// 		dbPipe.query( "SELECT NOW() - ? AS time_diff", rs -> { return rs.getInt(); } )
	}

	public static void main( String args[] ) {
		SimplestUseCase useCase = new SimplestUseCase();

	}
}

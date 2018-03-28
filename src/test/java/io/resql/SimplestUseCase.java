package io.resql;

import com.zaxxer.hikari.*;
import org.junit.Test;
import org.slf4j.*;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.*;

/**
 * Typical use case of reSql without any DI or something
 */
public class SimplestUseCase {
	private static final Logger log = LoggerFactory.getLogger( SimplestUseCase.class );

	public SimplestUseCase() {}

	private Map<Integer, String> getAllJdbcTypeNames() throws IllegalAccessException {
		Map<Integer, String> result = new HashMap<>();
		for (Field field : Types.class.getFields()) {
			result.put((Integer)field.get(null), field.getName());
		}
		return result;
	}

	@Test
	public void testSimplestUseCase() {
		HikariConfig config = new HikariConfig("/simple_use_case_hikari.properties" );
		HikariDataSource dataSource = new HikariDataSource( config );
		PostgresqlDbManager dbManager = new PostgresqlDbManager( dataSource );
		PostgresqlDbPipe pipe = dbManager.getPipe( log );
		pipe.execute("CREATE TABLE test_table( id SERIAL, text_data TEXT )" );
	}

	private void compareWatches() {
// 		dbPipe.query( "SELECT NOW() - ? AS time_diff", rs -> { return rs.getInt(); } )
	}

/*	public static void main( String args[] ) {
		SimplestUseCase useCase = new SimplestUseCase();
 	}*/
}

package io.resql;

import com.zaxxer.hikari.*;
import org.junit.Test;
import org.postgresql.core.TypeInfo;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * Typical use case of reSql without any DI or something
 */
public class SimplestUseCase {
	private static final Logger log = LoggerFactory.getLogger( SimplestUseCase.class );

	public SimplestUseCase() {}

	private Map<Integer, String> getAllJdbcTypeNames() throws IllegalAccessException {
		Map<Integer, String> result = new HashMap<Integer, String>();
		for (Field field : Types.class.getFields()) {
			result.put((Integer)field.get(null), field.getName());
		}
		return result;
	}

	@Test
	public void testSimplestUseCase() {
		HikariConfig config = new HikariConfig("/simple_use_case_hikari.properties" );
		HikariDataSource dataSource = new HikariDataSource( config );
		try ( Connection connection = dataSource.getConnection() ) {
			PgConnection pgconn = connection.unwrap( PgConnection.class );
			Statement st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery( "SELECT id, num FROM complex_data" );
			ResultSetMetaData meta = rs.getMetaData();
			TypeInfo typeInfo = pgconn.getTypeInfo();
			Map< String, Class<?>> typeMap = pgconn.getTypeMap();
			String className = meta.getColumnClassName( 2 );
			int columnType = meta.getColumnType( 2 );
			log.info( "Class name is {}, type is {}", className, getAllJdbcTypeNames().get(columnType));
			while ( rs.next() ) {
				Object obj = rs.getObject(  2  );
				log.info( "Объект:{}", obj );
			}
		} catch ( Exception e ) {
			log.error( "Exception while working with db", e );
		}
/*		PostgresqlDbManager dbManager = new PostgresqlDbManager( dataSource );
		PostgresqlDbPipe pipe = dbManager.getPipe( log );
		Object obj = pipe.select(As::single, Object::new,"SELECT 1"); */
	}

	private void compareWatches() {
// 		dbPipe.query( "SELECT NOW() - ? AS time_diff", rs -> { return rs.getInt(); } )
	}

/*	public static void main( String args[] ) {
		SimplestUseCase useCase = new SimplestUseCase();
 	}*/
}

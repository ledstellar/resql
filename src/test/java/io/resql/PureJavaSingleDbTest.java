package io.resql;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PureJavaSingleDbTest {
	private static final Logger log = LoggerFactory.getLogger( PureJavaSingleDbTest.class );
	private static HikariDataSource ds = new HikariDataSource();
	static {
		ds.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
		ds.setUsername("test");
		ds.setPassword("testpwd");
	}
	private static PostgresqlDbManager dbManager = new PostgresqlDbManager( ds );
	private static final PostgresqlDbPipe pipe = dbManager.getPipe( log );

	public PureJavaSingleDbTest() {}

	@Test
	public void simpleWalker() {
/*		StackWalker.getInstance().forEach( System.out::println );
		System.out.println();
		StackWalker.StackFrame frame = StackWalker.getInstance().walk( s -> s.skip(2).limit(1).findAny().orElse(  null ) );
		System.out.println( frame ); */
	}

	/**
	 * CREATE TABLE test(
	 * 		id INTEGER,
	 * 		text_value TEXT
	 * );
	 */
	@Test
	public void simpleTest() {
		int trials = 1_000_000;

		long start = System.currentTimeMillis();

		long a = 1;
		for (int i = 0; i < trials; i += 1) {
			a += 1;
		}

		long duration = System.currentTimeMillis() - start;
		System.out.println("Simple loop took " + duration + " ms");

		start = System.currentTimeMillis();

		a = 1;
		for (int i = 0; i < trials; i += 1) {
			a += 1;
//			StackWalker.getInstance().walk( s -> s.skip(2).limit(1).findAny().orElse(  null ) );
		}

		duration = System.currentTimeMillis() - start;
		System.out.println("Getting StackFrame took " + duration + " ms");

		start = System.currentTimeMillis();

		a = 1;
		for (int i = 0; i < trials; i += 1) {
			a += 1;
			Thread.currentThread().getId();
		}

		duration = System.currentTimeMillis() - start;
		System.out.println("Getting current thread took " + duration + " ms");

		start = System.currentTimeMillis();

		a = 1;
		for (int i = 0; i < trials; i += 1) {
			a += 1;
			Thread.currentThread().getStackTrace();
		}

		duration = System.currentTimeMillis() - start;
		System.out.println("Getting stack trace took " + duration + " ms");

		start = System.currentTimeMillis();

		a = 1;
		for (int i = 0; i < trials; i += 1) {
			a += 1;
			//noinspection ThrowableNotThrown
			(new Throwable()).getStackTrace();
		}

		duration = System.currentTimeMillis() - start;
		System.out.println("Getting throwable stack trace took " + duration + " ms");
	}
}

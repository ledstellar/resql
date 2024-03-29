package ru.resql.orm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import ru.resql.*;
import ru.resql.orm.converters.ConverterException;
import ru.resql.transactional.TransactionException;

import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.resql.TestSuite.*;
import static ru.resql.orm.RatedAnimeCharacter.*;

@Slf4j
@TestMethodOrder(SchemaDroppingMethodToTheEnd.class)
abstract class TestSelectsBase<DataSourceType extends DataSource> {
	private final PostgresqlDbPipe pipe;

	TestSelectsBase(String dbPropertyFileName, String dataSourceDescription, Logger log) throws IOException, SQLException {
		pipe = new PostgresqlDbManager(
			getDataSource(TestUtils.loadProperties(getClass(), dbPropertyFileName)),
			dataSourceDescription
		).getPipe(log);
		recreateTestSchema(pipe);
		pipe.execute("CREATE TYPE anime_character AS ENUM("
			+ "'Goku','AstroBoy','SpeedRacer','SpikeSpiegel','HimuraKenshin','NarutoUzumaki','EdwardElric',"
			+ "'Pikachu','SailorMoon','AyanamiRei','ShotaroKaneda','L','MotokoKusanagi','D','ArseneLupinIII')");
	}

	abstract DataSourceType getDataSource(Properties properties)  throws SQLException;

	@Test
	void testForEach() {
		pipe
			.select(String.class, "SELECT tablename FROM pg_tables WHERE schemaname = 'pg_catalog'")
			.forEach(this::checkTableName);
	}

	private void checkTableName(String tableName) {
		assertNotNull(tableName, "Table name can't be null");
		assertTrue(tableName.startsWith("pg_"), "PostgreSQL system table name should start with pg_");
	}

	@Test void testCollector() {
		List<String> tableNames = pipe
			.select(String.class, "SELECT tablename FROM pg_tables WHERE schemaname = 'pg_catalog'")
			.collect(Collectors.toList());
		assertNotNull(tableNames, "Request for system tables didn't returned any");
		assertTrue(tableNames.size() > 10, "Less than 10 system tables returned");
		tableNames.forEach(tableName -> assertTrue(tableName.startsWith("pg_"), tableName + " is likely not a system table name"));
	}

	@Test void testLimit() {
		List<String> tableNames = pipe
			.select(String.class, "SELECT tablename FROM pg_tables WHERE schemaname = 'pg_catalog'")
			.limit(5)
			.collect(Collectors.toList());
		assertNotNull(tableNames, "Request for system tables didn't returned any");
		assertEquals(5, tableNames.size(), "Limit operation doesn't applied correctly");
		tableNames.forEach(tableName -> assertTrue(tableName.startsWith("pg_"), tableName + " is likely not a system table name"));
	}

	@Test void testFilter() {
		List<String> attrNames = pipe
			.select(String.class, "SELECT tablename FROM pg_tables WHERE schemaname = 'pg_catalog'")
			.filter(name -> name.contains("attr"))
			.collect(Collectors.toList());
		assertTrue(attrNames.size() > 1, "Should be at least two system tables with 'attr' in name");
		for (String attrName : attrNames) {
			assertTrue(attrName.contains("attr"), "Filter should filter out names without 'attr'");
		}
	}

	/** Check that exception in filter doesn't lead to connection leak */
	@Test void testFilterException() {
		//noinspection ResultOfMethodCallIgnored
		assertThrows(
			ArithmeticException.class,
			() -> pipe
				.select(Integer.class, "SELECT s FROM generate_series(?, ?) AS s", 1, 20)
				.filter(el -> {
					return (100 / (el - 10)) % 2 == 0;	// ArithmeticException at 10
				})
				.collect(Collectors.toList())
		);
	}

	@Test void testCounter() {
		assertEquals(
			30,
			pipe
				.select(Integer.class, "SELECT s FROM generate_series(?, ?) AS s", 1, 30)
				.count(),
			"Wrong count function result"
		);
	}

	@Test void testIntegerArrayStream() {
		assertArrayEquals(
			new Integer[] {1, 2, 3},
			pipe
				.select(Integer[].class, "SELECT ARRAY[1, 2, 3]::INTEGER[]")
				.findAny()
				.orElse(null),
			"Wrong stream result"
		);
	}

	@Test void testTransaction() {
		assertThrows(
			TransactionException.class,
			() -> pipe.transactional((pipe) -> {
				// Create test table
				pipe.execute("CREATE TABLE t1(id INTEGER)");
				// Check table exists in transaction
				assertEquals(
					1,
					pipe.select(
						String.class,
						"SELECT tablename FROM pg_tables WHERE (schemaname, tablename) = (?, 't1')",
						TEST_SCHEMA_NAME
					).count()
				);
				// roll back transaction
				throw new IllegalArgumentException("Intentional exception to rollback");
			})
		);
		assertEquals(
		0,
			pipe.select(
				String.class,
				"SELECT tablename FROM pg_tables WHERE (schemaname, tablename) = (?, 't1')",
				TEST_SCHEMA_NAME
 			).count(),
			"Table still exists but it shouldn't as transaction must be rolled back due to IllegalArgumentException"
		);
	}

/* FIXME: implement support for this
	@Test void testEmpty4dArray() {
		int[][][][] arr = {{{{1,2},{3,4}},{{5,6},{7,8}}},{{{10,11},{12,13}},{{14,15},{16,17}}}};
		assertEquals(
			arr,
			pipe.select(
				int[][][][].class,
				"SELECT NULL::INTEGER[][][][]"
			).findFirst().orElseThrow(() -> new RuntimeException("Empty response instead of 4-dimensional array of int"))
		);
	}

	@Test void test4dArray() {
		int[][][][] arr = {{{{1,2},{3,4}},{{5,6},{7,8}}},{{{10,11},{12,13}},{{14,15},{16,17}}}};
		assertEquals(
			arr,
			pipe.select(
				int[][][][].class,
				"SELECT ARRAY[[[[1,2],[3,4]],[[5,6],[7,8]]],[[[10,11],[12,13]],[[14,15],[16,17]]]]"
			).findFirst().orElseThrow(() -> new RuntimeException("Empty response instead of 4-dimensional array of int"))
		);
	}
*/
	@Test void testNoneMatch() {
		assertTrue(
			pipe.select(
				Integer.class,
				"SELECT unnest(ARRAY[0, 2, 8, 10]::INTEGER[])").noneMatch(el -> (el % 2) == 1)
			)
		;
		assertFalse(
			pipe.select(
				Integer.class,
				"SELECT unnest(ARRAY[0, 2, 8, 10, 9]::INTEGER[])").noneMatch(el -> (el % 2) == 1
			)
		);
		assertTrue(
			pipe.select(
				Integer.class,
				"SELECT 1 LIMIT 0").noneMatch(el -> (el % 2) == 1
			)
		);
		//noinspection ResultOfMethodCallIgnored
		assertThrows(
			NullPointerException.class,
			() -> pipe.select(
				Integer.class,
				"SELECT unnest(ARRAY[0, 2, 8, 10, 9]::INTEGER[])"
			).noneMatch(el -> { throw new NullPointerException(); })
		);
		assertTrue(
			pipe.select(
				Integer.class,
				"SELECT 1 LIMIT 0"
			).noneMatch(el -> { throw new NullPointerException(); })
			// Predicate should not be called as no resultSet. So no exception should be thrown/
		);
	}

	@Test void nullFirstElement() {
		assertThrows(
			NullPointerException.class,
			() -> pipe.select(
				Integer.class,
				"SELECT unnest(ARRAY[null, -10, 0, 10]::INTEGER[])"
			).findFirst().orElseThrow(() -> new RuntimeException("Can't happen"))
		);
	}

	@Test void checkException() {
		assertThrows(
			NullPointerException.class,
			() -> pipe
				.select(Integer.class, "SELECT s FROM generate_series(?, ?) AS s", 1, 30)
				.skip(10).limit(10).forEach(result -> {
				if (result == 13) {
					throw new NullPointerException("Intended exception");
				}
			})
		);
	}

	@Test void checkCollectionsAreNotAllowed() {
		assertThrows(
			SqlException.class,
			() -> pipe
				.execute("SELECT ?", Arrays.asList(1, 2, 3))
		);
	}

/*
	@Test void checkUntypedParams() {
		pipe.execute("DROP TABLE IF EXISTS tmp");
		pipe.execute(
			"SELECT *"
			+ " INTO tmp"
			+ " FROM (VALUES"
				+ " (1, 'String', 1.01, TRUE),"
				+ " (2, 'Another string', NULL, FALSE),"
				+ " (3, 'String', 3.03, NULL),"
				+ " (4, NULL, 1.01, NULL),"
				+ " (5, 'String', 5.05, TRUE)"
			+ " ) AS t(id, txt, dbl, bl)"
		);
		pipe.execute("INSERT INTO tmp(id, txt, dbl) VALUES (?, ?, ?)", 5, null, null);
	}
*/
	@Test void streamingTest() {
		long recordCount = 500_000;
		long[] lastValue = new long[]{0};
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long totalMemory = runtime.totalMemory();
		long totalMemoryBefore = totalMemory / (1024 * 1024);
		long maxMemoryAllowed = totalMemory / (1024 * 1024) + 10; // not more than 10Mb overhead
		assertEquals(
			recordCount,
			pipe.select(Long.class, "SELECT * FROM generate_series(1, ?)", recordCount)
				.setFetchSize(10_000)
				.peek(l -> {
					if (l % 100_000L == 0L) {
						runtime.gc();
						long totalMemoryInMb = runtime.totalMemory() / (1024 * 1024);
						assertTrue(
							totalMemoryInMb <= maxMemoryAllowed,
							"Consumed memory increased from " + totalMemoryBefore + "Mb to "
							+ totalMemoryInMb + "Mb but expected not more than " + maxMemoryAllowed
							+ "Mb.\nProbably JDBC streaming is not enabled"
						);
					}
				})
				.filter(l ->  l == lastValue[0] + 1)
				.count()
		);
	}

	@Test void checkOrm() {
		class TestClass {
			public Integer[] intArray;
			@SuppressWarnings("unused")
			private String str;
			long longValue;
			int arraySize;
		}
		Integer[] intArray = {-10, 0, 10, null, 20};
		String str = "the string";
		long longValue = 1234L;
		TestClass impl = pipe
			.select(
				TestClass::new,
				// Note no PostgreSQL-side conversions
				"SELECT ? AS int_array, cardinality(?) AS arraySize, ? AS str, ? AS long_value",
				intArray, intArray, str, longValue
			).findFirst()
			.orElse(null);
		assertNotNull(impl, "No result returned");
		assertArrayEquals(intArray, impl.intArray);
		assertEquals(str, impl.str);
		assertEquals(longValue, impl.longValue);
		assertEquals(intArray.length, impl.arraySize);
	}

	@Test void checkConverterNullException() {
		class IntKeeperClass {
			@SuppressWarnings("unused")
			int theInt;
		}
		assertThrows(
			SqlException.class,
			() -> {
				@SuppressWarnings("unused")
				List<IntKeeperClass> ignored = pipe
					.select(
						IntKeeperClass::new,
					// Note no PostgreSQL-side conversions
						"SELECT unnest(ARRAY[10, 20, null, 30]::INTEGER[]) AS the_int"
					).collect(Collectors.toList());
			}
		);
	}

	@Test void testTaggedEnumArrays() {
		assertArrayEquals(
			new RatedAnimeCharacter[][]{
				new RatedAnimeCharacter[]{SpeedRacer, SpikeSpiegel},
				new RatedAnimeCharacter[]{HimuraKenshin, NarutoUzumaki, AyanamiRei},
				new RatedAnimeCharacter[]{ArseneLupinIII, ShotaroKaneda},
				new RatedAnimeCharacter[]{MotokoKusanagi, D}
			},
			pipe.select(
				RatedAnimeCharacter[].class,
				"WITH a(v) AS (VALUES (ARRAY[3,4]), (ARRAY[5,6,10]), (ARRAY[15,11]), (ARRAY[13,14])) SELECT v::INTEGER[] FROM a"
			).collect(Collectors.toList()).toArray()
			// TODO: .collect(...).toArray() can be replaced to single .toArray() but .toArray() not implemented yet
		);
	}

	@Test void testTaggedWithException() {
		//noinspection ResultOfMethodCallIgnored
		assertThrows(
			ConverterException.class,
			() -> pipe.select(
				RatedAnimeCharacter.class,
				// enum with tag 100 doesn't exists
				"SELECT * FROM unnest(ARRAY[5, 100, 10]::INTEGER[])"
			).count()
		);
	}

	@Test void testTagged() {
		assertEquals(
			3,
			pipe.select(
				RatedAnimeCharacter.class,
				"SELECT * FROM unnest(ARRAY[" +
				 AstroBoy.getTag() + ", " + ArseneLupinIII.getTag() + ", " + AyanamiRei.getTag()
				 + "]::INTEGER[])"
			).peek(state -> assertTrue(state.name().startsWith("A")))
				.count(),
			"Wrong number of constants"
		);
	}

	@Test void testSingleUntaggedEnum() {
		assertEquals(
			AnimeCharacter.ShotaroKaneda,
			pipe.select(
				AnimeCharacter.class,
				"SELECT 'ShotaroKaneda'::anime_character"
			 ).findFirst().orElse(null)
		);
	}

	@Test void testTypedEnumArray() {
		AnimeCharacter[] reservedStates = pipe.select(
				AnimeCharacter[].class,
			"SELECT ARRAY['AstroBoy', 'ArseneLupinIII', 'AyanamiRei']::anime_character[]"
			).findAny()
			.orElse(null);
		assertArrayEquals(
			new AnimeCharacter[]{AnimeCharacter.AstroBoy, AnimeCharacter.ArseneLupinIII, AnimeCharacter.AyanamiRei},
			reservedStates
		);
	}

	@Test void atLastDropTestSchema() {
		dropTestSchema(pipe);
	}
}

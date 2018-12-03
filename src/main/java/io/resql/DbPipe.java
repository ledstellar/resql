package io.resql;

import java.sql.ResultSetMetaData;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Database connection(s) tied to particular class.
 */
public interface DbPipe {
	/**
	 * Execute given (supposedly update) query
	 * @param sql the query
	 * @param params query params
	 * @return number of records processed (if database returns this info)
	 */
	int execute( CharSequence sql, Object... params );

 	<Type> void batch(String sql, Batcher<Type> batcherImpl);

	<Type> void batch(QueryBuilder<Type> sqlBuilder, Batcher<Type> batcherImpl);

	<OrmType> Stream<OrmType> select(Supplier<OrmType> factory, CharSequence sql, Object... params);

	ResultSetMetaData getMetaData(String query);
}

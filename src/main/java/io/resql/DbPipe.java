package io.resql;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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

	/**
	 * Select type query. Select none, single or set of records from database and process it in some manner
	 * @param processor
	 * @param sql
	 * @param params
	 * @param <T>
	 */
	<T,ResultT> ResultT select(Processor<T,ResultT> processor, Supplier<T> factory, CharSequence sql, Object... params);
}

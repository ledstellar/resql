package io.resql;

import java.util.Collection;
import java.util.function.*;
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
	 * Select type query. Select none, single or set of records from database and process it in some manner.
	 * This function takes no argument constructor and uses direct access to class members (with any visibility).
	 * When target class isExists no such constructors then use {@link #select(Processor, Class, CharSequence, Object...)}
	 * function.
	 * This method comparatively more performant constructor than alternative one.
	 * @param processor specific resultset processor
	 * @param factory reference to no argument constructor of target ORM class
	 * @param sql query to database
	 * @param params parameters to the query if any
	 * @param <T> target ORM class
	 * @param <ResultT> call result type
	 */
	<T,ResultT> ResultT select( Processor<T,ResultT> processor, Supplier<T> factory, CharSequence sql, Object... params);

	/**
	 * Select type query. Select none, single or set of records from database and process it in some manner.
	 * This function takes target ORM class and scan it for appropriate constructor. If you prefer class with no args
	 * (maybe default) constructor and direct
	 * This method comparatively less performant constructor than alternative one.
	 * @param processor specific resultset processor
	 * @param targetClass target ORM class
	 * @param sql query to database
	 * @param params parameters to the query if any
	 * @param <T> target ORM class
	 * @param <ResultT> call result type
	 */
	<T,ResultT> ResultT select( Processor<T,ResultT> processor, Class<T> targetClass, CharSequence sql, Object... params);
}

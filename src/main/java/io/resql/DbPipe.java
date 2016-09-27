package io.resql;

import io.resql.extractors.Extractor;
import org.slf4j.*;

import java.util.function.Supplier;
import java.util.stream.*;

/**
 * Database connection(s) tied to particular class.
 */
public interface DbPipe {
	Marker SELECT = MarkerFactory.getMarker( "SELECT" );
	Marker UPDATE = MarkerFactory.getMarker( "UPDATE" );

	/**
	 * Executes update statements.
	 * @param sql SQL query
	 * @param params query params if any
	 * @return number of records affected
	 */
	int update( CharSequence sql, Object... params );

	/**
	 * Execute update statement with updating single record only. If statement updates multiple or none records
	 * exception is thrown
	 * @param sql SQL query
	 * @param params query params if any
	 * @throws SqlException
	 */
	default void updateSingle( CharSequence sql, Object... params ) {
		int processed = update( sql, params );
		if ( processed != 1 ) {
			throw new SqlException( String.format( "Expected updating of single record only but updated %d ", processed ) );
		}
	}

	/**
	 * Execute DDL statements or another kinds of statements producing no result.
	 * @param sql SQL DDL query
	 * @param params query params if any
	 */
	void execute( CharSequence sql, Object params );

	/**
	 * Execute query and process it in some way, producing single values, collections of values etc.
	 * @param extractor
	 * @param sql
	 * @param params
	 * @param <ReturnType>
	 * @return
	 */
	<ReturnType> ReturnType query( Extractor<ReturnType> extractor, CharSequence sql, Object... params );

	IntStream queryForInt( CharSequence sql, Object... params );
	LongStream queryForLong( CharSequence sql, Object... params );

	< OrmType > Stream< OrmType > query( Supplier< OrmType > factory, CharSequence sql, Object... params );

	< OrmType, ReturnType > ReturnType query(
			Supplier< OrmType > factory, OrmExtractor< OrmType, ReturnType > extractor,
			CharSequence sql, Object... params
	);
}

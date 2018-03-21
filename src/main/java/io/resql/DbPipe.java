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
	 * Marks SELECT-only queries. I.e. {@link #query(CharSequence, Class, Object...)}, {@link #query(Class, Object...)},
	 * {@link #queryForNullable(CharSequence, Supplier, Object...)}, {@link #queryForNullable(Supplier, Object...)},
	 * {@link #queryForInt(CharSequence, Object...)}, {@link #queryUpperForInt(Object...)},
	 * {@link #queryForLong(CharSequence, Object...)}, {@link #queryUpperForLong(Object...)},
	 * {@link #queryForSingle(CharSequence, Supplier, Object...)}, {@link #queryForSingle(Supplier, Object...)}
	 */
// 	public static final Marker SELECT = MarkerFactory.getMarker( "SELECT" );

	/** Marks UPDATE-only queries */
//	public static final Marker UPDATE = MarkerFactory.getMarker( "UPDATE" );

	<T> T queryForNullable( CharSequence sql, Supplier<T> factory, Object... params );
	<T> T queryForNullable( Supplier<T> factory, Object... params );
	<T> T queryForSingle( CharSequence sql, Supplier<T> type, Object... params );
	<T> T queryForSingle( Supplier<T> type, Object... params );
	<T> Stream<T> query( CharSequence sql, Class<T> clazz, Object... params );
	<T> Stream<T> query( Class<T> clazz, Object... params );
	IntStream queryForInt( CharSequence sql, Object... params );
	IntStream queryUpperForInt( Object... params );
	LongStream queryForLong( CharSequence sql, Object... params );
	LongStream queryUpperForLong( Object... params );
	<T> void insert( T dto );
	<T> void insert( Collection< T > data );
	<T> void insert( T[] data );
	<T> void insert( Supplier<T> factory );
	<T> void update( T to );
	<T> void upsert( T dto );
	<T> void updateChanged( T before, T after );
	<T> void updateChanged( T after );
	<T> void execute( CharSequence sql, Object... params );
	<T> void executeUpper( Object... params );
	<T> void executeSingle( CharSequence sql, Object... params );
	<T> void executeUpperSingle( Object... params );

	/* V3 methods begin */

	/**
	 * Select type query. Select none, single or set of records from database and process it in some manner
	 * @param processor
	 * @param sql
	 * @param params
	 * @param <T>
	 */
	<T,ResultT> ResultT select(Processor<T,ResultT> processor, Supplier<T> factory, CharSequence sql, Object... params);
}

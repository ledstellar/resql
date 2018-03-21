package io.resql;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class PostgresqlDbPipe implements DbPipe {
	private PostgresqlDbManager dbManager;
	private Logger log;

	/**
	 * Constructor for autowiring. No logger mentioned in parameters.
	 * It should be set up later by Annotation Processor.
	 * @param dbManager DbManager instance
	 */
	PostgresqlDbPipe( PostgresqlDbManager dbManager ) {
		this( dbManager, null );
	}

	PostgresqlDbPipe(PostgresqlDbManager dbManager, Logger log ) {
		this.dbManager = dbManager;
		this.log = log;
	}

	/**
	 * Logger setter for Annotation Processors.
	 * @param logger  
	 */
	public void setLogger( Logger logger ) {
		this.log = logger;
	}

	@Override
	public <T> T queryForNullable(CharSequence sql, Supplier<T> factory, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> T queryForNullable(Supplier<T> factory, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> T queryForSingle(CharSequence sql, Supplier<T> type, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> T queryForSingle(Supplier<T> type, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> Stream<T> query(CharSequence sql, Class<T> clazz, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> Stream<T> query(Class<T> clazz, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void insert(T dto) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void insert(Collection<T> data) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void insert(T[] data) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void insert(Supplier<T> factory) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void update(T to) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void upsert(T dto) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void updateChanged(T before, T after) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void updateChanged(T after) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void execute(CharSequence sql, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public < T > void executeUpper( Object... params ) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public <T> void executeSingle(CharSequence sql, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public < T > void executeUpperSingle( Object... params ) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public IntStream queryForInt(CharSequence sql, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public IntStream queryUpperForInt( Object... params ) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public LongStream queryForLong(CharSequence sql, Object... params) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}

	@Override
	public LongStream queryUpperForLong( Object... params ) {
		// TODO: implement
		throw new RuntimeException( "Not implemented yet" );
	}


	private String reconstructQuery( CharSequence sql, Object[] params) {
		return null;
	}

	private void injectParams( PreparedStatement ps, Object[] params ) throws SQLException {
		int i = 1;
		for ( Object param : params ) {
			ps.setObject( i ++, param );
		}
	}

	protected Connection getConnection() throws SQLException {
		return dbManager.getConnection();
	}

	@Override
	public <T,ResultT> ResultT select(Processor<T,ResultT> processor, Supplier<T> factory, CharSequence sql, Object... params) {
		return null;
	}
}

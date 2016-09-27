package io.resql;

import io.resql.extractors.Extractor;
import org.slf4j.Logger;

import java.sql.*;
import java.util.function.Supplier;
import java.util.stream.*;

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
	public int update(CharSequence sql, Object... params) {
		return 0;
	}

	@Override
	public void updateSingle(CharSequence sql, Object... params) {

	}

	@Override
	public void execute(CharSequence sql, Object params) {

	}

	@Override
	public <ReturnType> ReturnType query(Extractor<ReturnType> extractor, CharSequence sql, Object... params) {
		String fullQuery = null;
		try {
			if ( log.isDebugEnabled( SELECT ) ) {
				fullQuery = reconstructQuery( sql, params );
				log.debug( SELECT, fullQuery );
			}
			Connection connection = getConnection();
			PreparedStatement ps = connection.prepareStatement( sql.toString() );
			injectParams( ps, params );
			ResultSet rs = ps.executeQuery();
			return extractor.extract( rs, log );
		} catch ( SQLException sqle ) {
			// construct full query in case of problems only
			throw new SqlException(
					"Error executing query:\n" + fullQuery == null ? reconstructQuery( sql, params ) : fullQuery,
					sqle
			);
		}
	}

	@Override
	public IntStream queryForInt(CharSequence sql, Object... params) {
		return null;
	}

	@Override
	public LongStream queryForLong(CharSequence sql, Object... params) {
		return null;
	}

	@Override
	public <OrmType> Stream<OrmType> query(Supplier<OrmType> factory, CharSequence sql, Object... params) {
		return null;
	}

	@Override
	public <OrmType, ReturnType> ReturnType query(Supplier<OrmType> factory, OrmExtractor<OrmType, ReturnType> extractor, CharSequence sql, Object... params) {
		return null;
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
}

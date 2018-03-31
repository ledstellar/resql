package io.resql;

import org.slf4j.Logger;

import java.sql.*;
import java.util.function.Supplier;

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
	public int execute(CharSequence sql, Object... params) {
		try (Connection connection = dbManager.getConnection()) {
			Statement statement;
			boolean resultCountOrSetFlag;
			if (params == null) {
				statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
				resultCountOrSetFlag = statement.execute(sql.toString());
			} else {
				PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
				statement = preparedStatement;
				fillStatementWithParams(preparedStatement, params);
				resultCountOrSetFlag = preparedStatement.execute();
			}
			debugSql(sql,null);
			if (resultCountOrSetFlag) {
				throw new SqlException("Unexpected ResultSet received in execute call. Use queryXXX(...) functions instead");
			}
			return statement.getUpdateCount();
		} catch ( SQLException sqle ) {
			throw new SqlException( "Error executing " + sql, sqle );	// TODO: implement
		}
	}

	private void debugSql(CharSequence sql, Object[] params) {
		if (log.isDebugEnabled()) {
			log.debug(getCallerSrcLine()+(params==null?sql:injectParamsForDebug(sql,params)));
		}
	}

	private String getCallerSrcLine() {
		StackWalker.StackFrame frame = StackWalker.getInstance().walk(
			s -> s.dropWhile( f -> f.getClassName().endsWith( ".PostgresqlDbPipe" ) )
				.limit(1)
				.findAny()
				.orElse(  null ) );
		if (frame==null) {
			return "";
		}
		return "/*(" + frame.getFileName() + ':' + frame.getLineNumber() + ")*/ ";
	}

	private void fillStatementWithParams(PreparedStatement statement, Object[] params) throws SQLException {
		int index = 1;
		for ( Object param : params ) {
			statement.setObject(index++, param);
		}
	}

	private String injectParamsForDebug(CharSequence sql, Object[] params) {
		// TODO: implement
		return "";
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

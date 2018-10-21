package io.resql;

  import io.resql.orm.*;
import org.slf4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

public class PostgresqlDbPipe implements DbPipe {
	private PostgresqlDbManager dbManager;
	private Logger log;

	/**
	 * Constructor for autowiring. No logger mentioned in parameters.
	 * It should be set up later by Annotation Processor.
	 *
	 * @param dbManager DbManager instance
	 */
	PostgresqlDbPipe(PostgresqlDbManager dbManager) {
		this(dbManager, null);
	}

	PostgresqlDbPipe(PostgresqlDbManager dbManager, Logger log) {
		this.dbManager = dbManager;
		this.log = log;
	}

	@Override
	public int execute(CharSequence sql, Object... params) {
		try (Connection connection = dbManager.getConnection()) {
			Statement statement = createStatementAndExecute(connection, sql.toString(), params, false);
			debugSql(sql, null);
			return statement.getUpdateCount();
		} catch (SQLException sqlException) {
			throw new SqlException("Error executing " + getDebugRepresentation(sql, params), sqlException);    // TODO: implement detailed logging
		}
	}

	private void checkResultType(boolean isResultSetAwaiting, boolean isResultSetActual) {
		if (! isResultSetAwaiting && isResultSetActual) {
			throw new ConstraintException("Unexpected ResultSet received!");
		}
	}

	private Statement createStatementAndExecute(Connection connection, CharSequence sql, Object[] params, boolean isResultSetAwaiting) throws SQLException {
		String sqlString = sql.toString();
		if (params == null) {
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			checkResultType(isResultSetAwaiting, statement.execute(sqlString));
			return statement;
		} else {
			PreparedStatement preparedStatement = connection.prepareStatement(sqlString, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			fillStatementWithParams(preparedStatement, params);
			checkResultType(isResultSetAwaiting, preparedStatement.execute());
			return preparedStatement;
		}
	}

	/**
	 * Log SQL query in user format for debug purposes.
	 * @param sql SQL query
	 * @param params SQL query params
	 * TODO: implement and remove warning suppression
	 */
	@SuppressWarnings("SameParameterValue")
	private void debugSql(CharSequence sql, Object[] params) {
		if (log.isDebugEnabled()) {
			log.debug(getDebugRepresentation(sql, params));
		}
	}

	private String getDebugRepresentation(CharSequence sql, Object[] params) {
		return getCallerSrcLine() + (params == null || params.length == 0 ? sql : injectParamsForDebug(sql, params));
	}

	private String getCallerSrcLine() {
		StackWalker.StackFrame frame = StackWalker.getInstance().walk(
			s -> s.dropWhile(f -> f.getClassName().endsWith(".PostgresqlDbPipe"))
				.limit(1)
				.findAny()
				.orElse(null));
		if (frame == null) {
			return "";
		}
		return "/*(" + frame.getFileName() + ':' + frame.getLineNumber() + ")*/ ";
	}

	private void fillStatementWithParams(PreparedStatement statement, Object[] params) throws SQLException {
		int index = 1;
		for (Object param : params) {
			statement.setObject(index++, param);
		}
	}

	private String injectParamsForDebug(CharSequence sql, Object[] params) {
		// TODO: implement
		return "";
	}

	private String reconstructQuery(CharSequence sql, Object[] params) {
		return null;
	}

	private void injectParams(PreparedStatement ps, Object[] params) throws SQLException {
		int i = 1;
		for (Object param : params) {
			ps.setObject(i++, param);
		}
	}

	private <T, ResultT> ResultT internalSelect(Processor<T, ResultT> processor, Supplier<T> factory, Class<T> targetClass, CharSequence sql, Object... params) {
		try (Connection connection = dbManager.getConnection()) {
			Statement statement = createStatementAndExecute(connection, sql, params, true);
			ResultSet resultSet = statement.getResultSet();
			Accessor accessor = dbManager.accessorFactory.createOrGet(sql, getColumnTypes(resultSet.getMetaData()), factory, targetClass);
			return processor.process(new ResultSetSupplier<>(resultSet, accessor));
		} catch (ClassMappingException | SQLException sqle) {
			throw new SqlException("Error executing " + sql, sqle);    // TODO: implement detailed logging
		}
	}

	private LinkedHashMap<String, Integer> getColumnTypes(ResultSetMetaData metaData) throws SQLException {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>(metaData.getColumnCount());
		int columnCount = metaData.getColumnCount();
		for (int columnIndex = 1; columnIndex <= columnCount; ++ columnIndex) {
			map.put(metaData.getColumnName(columnIndex), metaData.getColumnType(columnIndex));
		}
		return map;
	}

	@Override
	public <T, ResultT> ResultT select(Processor<T, ResultT> processor, Supplier<T> factory, CharSequence sql, Object... params) {
		return internalSelect(processor, factory, null, sql, params);
	}

	@Override
	public <T, ResultT> ResultT select(Processor<T, ResultT> processor, Class<T> targetClass, CharSequence sql, Object... params) {
		return internalSelect(processor, null, targetClass, sql, params);
	}

	@Override
	public <Type> void batch(String sql, Batcher<Type> batcherImpl) {
		//  TODO: implement
	}

	@Override
	public <Type> void batch(QueryBuilder<Type> queryBuilder, Batcher<Type> batcherImpl) {
		//  TODO: implement
	}

	@Override
	public ResultSetMetaData getMetaData(String query) {
		// TODO: implement
		return null;
	}
}

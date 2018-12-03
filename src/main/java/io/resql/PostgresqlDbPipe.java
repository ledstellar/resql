package io.resql;

  import io.resql.orm.*;
  import io.resql.orm.stream.SqlStream;
  import org.slf4j.Logger;

  import javax.sql.DataSource;
  import java.sql.*;
import java.util.*;
import java.util.function.Supplier;
  import java.util.stream.*;

public class PostgresqlDbPipe implements DbPipe {
	private Logger log;
	private AccessorFactory accessorFactory;
	private DataSource dataSource;

	PostgresqlDbPipe(DataSource dataSource, AccessorFactory accessorFactory, Logger log) {
		this.dataSource = dataSource;
		this.accessorFactory = accessorFactory;
		this.log = log;
	}

	@Override
	public int execute(CharSequence sql, Object... params) {
		try (
			Connection connection = dataSource.getConnection();
			Statement statement = createStatementAndExecute(connection, sql, params, false)
		) {
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

	public static void fillStatementWithParams(PreparedStatement statement, Object[] params) throws SQLException {
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

	public static LinkedHashMap<String, String> getColumnTypes(ResultSetMetaData metaData) throws SQLException {
		LinkedHashMap<String, String> map = new LinkedHashMap<>(metaData.getColumnCount());
		int columnCount = metaData.getColumnCount();
		for (int columnIndex = 1; columnIndex <= columnCount; ++ columnIndex) {
			map.put(metaData.getColumnName(columnIndex), metaData.getColumnTypeName(columnIndex));
		}
		return map;
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

	@Override
	public <OrmType> Stream<OrmType> select(Supplier<OrmType> factory, CharSequence sql, Object... params) {
		return new SqlStream<>(dataSource, accessorFactory, log, sql, params, factory);
	}
}

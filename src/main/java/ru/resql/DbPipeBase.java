package ru.resql;

import org.slf4j.Logger;
import ru.resql.orm.stream.*;
import ru.resql.transactional.TransactionalPipe;
import ru.resql.util.SqlQueryDebugFormatter;

import java.sql.*;
import java.util.Collection;
import java.util.function.Supplier;

import static ru.resql.DbManager.RESQL_SELECT;

public abstract class DbPipeBase implements TransactionalPipe {
	protected final Logger log;
	protected final DbManager dbManager;

	protected DbPipeBase(DbManager dbManager, Logger log) {
		this.dbManager = dbManager;
		this.log = log;
	}

	protected abstract ConnectionWrapper getConnectionWrapper(boolean isReadOnly);

	SelectWithParamSqlDataSource getReadOnlySqlDataSource(CharSequence sql, Object[] args) {
		return new SelectWithParamSqlDataSource(getConnectionWrapper(true), log, sql, args);
	}

	@Override
	public <ReturnType> OrmSqlStreamRecordSource<ReturnType> select(Supplier<ReturnType> factory, CharSequence sql, Object... args) {
		return new OrmSqlStreamRecordSource<>(
			dbManager.getConverterFrames(), getReadOnlySqlDataSource(sql, args),
			dbManager.getAccessorFactory(), factory
		);
	}

	@Override
	public <ElementType> ObjectSqlStreamRecordSource<ElementType> select(Class<ElementType> clazz, CharSequence sql, Object... args) {
		return new ObjectSqlStreamRecordSource<>(
			clazz, getReadOnlySqlDataSource(sql, args),
			dbManager.getConverterFrames()
		);
	}

	@Override
	public IntSqlStreamRecordSource selectIntegers(CharSequence sql, Object... args) {
		return new IntSqlStreamRecordSource(getReadOnlySqlDataSource(sql, args), dbManager.getConverterFrames());
	}

	@Override
	public void batchUpsert(Collection<?> data) {
/*		SqlDataSource batchSyncDataSource = new BatchSyncDataSource();
		dbManager.getAccessorFactory().createOrGet( */

	}

	public int execute(CharSequence sql, Object... params) {
		ConnectionWrapper connectionWrapper = getConnectionWrapper(false);
		try (
			Statement statement = createStatementAndExecute(connectionWrapper.getConnection(), sql, params)
		) {
			debugSql(sql, params);
			return statement.getUpdateCount();
		} catch (SQLException sqlException) {
			SqlException sqle = new SqlException("Error executing query", sqlException);
			sqle.setRequestSql(SqlQueryDebugFormatter.getDebugRepresentation(sql.toString(), params));
			throw sqle;
		} finally {
			connectionWrapper.close();
		}
	}

	private Statement createStatementAndExecute(Connection connection, CharSequence sql, Object[] params) throws SQLException {
		String sqlString = sql.toString();
		if (params.length == 0) {
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			checkResultType(statement.execute(sqlString));
			return statement;
		} else {
			PreparedStatement preparedStatement = connection.prepareStatement(sqlString, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			fillStatementWithParams(preparedStatement, params);
			checkResultType(preparedStatement.execute());
			return preparedStatement;
		}
	}

	private static void fillStatementWithParams(PreparedStatement statement, Object[] params) throws SQLException {
		int index = 1;
		for (Object param : params) {
			statement.setObject(index++, param);
		}
	}

	/**
	 * Log SQL query in user format for debug purposes.
	 * @param sql SQL query
	 * @param params SQL query params
	 * TODO: implement and remove warning suppression
	 */
	private void debugSql(CharSequence sql, Object[] params) {
		if (log.isDebugEnabled(RESQL_SELECT)) {
			log.debug(RESQL_SELECT, SqlQueryDebugFormatter.getDebugRepresentation(sql.toString(), params));
		}
	}

	private void checkResultType(boolean isResultSetActual) {
		if (isResultSetActual) {
			throw new ConstraintException("Unexpected ResultSet received!");
		}
	}
}

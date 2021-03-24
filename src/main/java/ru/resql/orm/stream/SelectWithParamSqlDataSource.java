package ru.resql.orm.stream;

import org.slf4j.Logger;
import ru.resql.*;
import ru.resql.orm.vendor.postgresql.PgType;
import ru.resql.util.*;

import java.sql.*;
import java.util.Collection;

import static ru.resql.DbManager.RESQL_SELECT;
import static ru.resql.orm.vendor.postgresql.PgType.paramJavaClassToDefaultDbType;

public class SelectWithParamSqlDataSource extends SqlDataSource {
	private static final int DEFAULT_FETCH_SIZE = 5_000;
	protected final CharSequence sql;
	private final Object[] params;
	private boolean isConnectionClosed;
	private int fetchSize = DEFAULT_FETCH_SIZE;
	protected ResultSet resultSet;

	public SelectWithParamSqlDataSource(ConnectionWrapper connectionSource, Logger log, CharSequence sql, Object[] params) {
		super(connectionSource, log);
		this.sql = sql;
		this.params = params;
	}

	public String getQuery() {
		return sql.toString();
	}

	public Object getAccessorKey() {
		return getQuery();
	}

	@Override
	public ResultSetMetaData getResultSetMetaData() throws SQLException {
		return resultSet.getMetaData();
	}

	@Override
	public boolean isOptionalFields() {
		return false;
	}

	public void close() {
		super.close();
		isConnectionClosed = true;
	}

	protected boolean runQueryIfNotYet() {
		if (isConnectionClosed) {
			throw new IllegalStateException("Internal error: tried use closed SQL stream");
		}
		if (connectionSource.initiateIfNeed()) {
			try {
				executeReadOnlyQuery();
			} catch (SQLException sqle) {
				String dataSourceDescription = connectionSource.getConnectionSourceDescription();
				throw new SqlException(
					"Error executing SQL " + (dataSourceDescription == null ? "" : (" on " + dataSourceDescription)),
					sqle
				);
			}
			return true;
		}
		return false;
	}

	private void executeReadOnlyQuery() throws SQLException {
		Connection connection = connectionSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		preparedStatement.setFetchSize(fetchSize);
		fillStatementWithParams(preparedStatement, params);
		if (log.isDebugEnabled(RESQL_SELECT)) {
			// Show the line with SQL query
			log.debug(RESQL_SELECT, SqlQueryDebugFormatter.getDebugRepresentation(sql.toString(), params));
		}
		resultSet = preparedStatement.executeQuery();
	}

	private void fillStatementWithParams(PreparedStatement statement, Object[] params) {
		int index = 1;
		try {
			for (Object param : params) {
				Class<?> paramClass = param.getClass();
				if (paramClass.isArray()) {
					statement.setArray(index, convertToSqlArray(param, paramClass, index));
				} else if (Collection.class.isAssignableFrom(paramClass)) {
					// TODO: implement
				} else if (paramClass.isEnum()) {
					// TODO: implement
				} else {
					statement.setObject(index, param);
				}
				++ index;
			}
		} catch (SQLException sqle) {
			throw new SqlException("Error setting parameter #" + (index - 1) + ": '" + params[index - 2] + "'", sqle);
		}
	}

	protected void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	private Array convertToSqlArray(Object param, Class<?> paramClass, int columnIndex) throws SQLException {
		Class<?> elementClass = paramClass.getComponentType();
		String defaultDbType = PgType.paramJavaClassToDefaultDbType.get(elementClass);
		Object[] paramArray;
		if (defaultDbType == null) {
			Class<?> objectClassOfPrimitive = PrimitiveUtil.primitiveToObjects.get(elementClass);
			if (objectClassOfPrimitive == null) {
				throw new SqlException(
					"Cannot use array of " + elementClass.getName() + " as the parameter #"
						+ columnIndex + ": no appropriate conversion can be found"
				);
			}
			// parameter is array of primitives. We need to convert it into array of boxed objects
			defaultDbType = paramJavaClassToDefaultDbType.get(objectClassOfPrimitive);
			if (Integer.class.equals(objectClassOfPrimitive)) {
				int[] srcIntArray = (int[])param;
				paramArray = new Integer[srcIntArray.length];
				for (int i = srcIntArray.length - 1; i >= 0; -- i) {
					paramArray[i] = srcIntArray[i];
				}
			} else if (Long.class.equals(objectClassOfPrimitive)) {
				long[] srcLongArray = (long[])param;
				paramArray = new Long[srcLongArray.length];
				for (int i = srcLongArray.length - 1; i >= 0; -- i) {
					paramArray[i] = srcLongArray[i];
				}
			} else if (Boolean.class.equals(objectClassOfPrimitive)) {
				boolean[] srcBooleanArray = (boolean[])param;
				paramArray = new Boolean[srcBooleanArray.length];
				for (int i = srcBooleanArray.length - 1; i >= 0; -- i) {
					paramArray[i] = srcBooleanArray[i];
				}
			} else if (Double.class.equals(objectClassOfPrimitive)) {
				double[] srcDoubleArray = (double[])param;
				paramArray = new Double[srcDoubleArray.length];
				for (int i = srcDoubleArray.length - 1; i >= 0; -- i) {
					paramArray[i] = srcDoubleArray[i];
				}
			} else if (Float.class.equals(objectClassOfPrimitive)) {
				float[] srcFloatArray = (float[])param;
				paramArray = new Float[srcFloatArray.length];
				for (int i = srcFloatArray.length - 1; i >= 0; -- i) {
					paramArray[i] = srcFloatArray[i];
				}
			} else {
				throw new SqlException("Internal error: type " + elementClass.getName() + " cannot be used as parameter");
			}
		} else {
			paramArray = (Object[])param;
		}
		return connectionSource.getConnection().createArrayOf(defaultDbType, paramArray);
	}

	public String getQueryDebug() {
		// TODO: cache this formatter!
		return SqlQueryDebugFormatter.getDebugRepresentation(sql.toString(), params);
	}
}

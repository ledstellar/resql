package io.resql.orm;

import java.sql.*;
import java.util.function.Supplier;

public class AccessorFactory {
	/**
	 * Create new accessor or find and return existing one. Either factory or targetClass parameters should be set (but not both).
	 * @param sql SQL query for this accessor. Used to distinguish exact accessor if there are many for given factoty or targetClass
	 * @param metaData SQL query metadata. Used when creating new accessor
	 * @param factory supplier of ORM class instances. Can be <code>null></code>. If not <code>null</code> then accessor will use direct reflection access to
	 * ORM class members
	 * @param targetClass class of target ORM instances. Can be <code>null></code>. If not <code>null</code> then accessor will scan class and its ancestors
	 * for constructor with appropriate arguments
	 * @param <T> ORM class
	 * @return accessor for ORM class instance initialization
	 */
	public <T> Accessor createOrGet( String sql, ResultSetMetaData metaData, Supplier<T> factory, Class<T> targetClass) {
		return null;	// TODO: implement
	}
}
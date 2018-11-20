package io.resql.orm;

import com.sun.jdi.Value;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

public class AccessorFactory {
	/**
	 * Aggregate storage for all type accessors
	 * Map of (POJO class) to AccessorSet(Map of (SQL query) to (Accessor)).
	 * This nesting allows to effectively recognize/remove excessibly generated Accessors due to dynamic SQL queries.
	 **/
	private HashMap<Class<?>, AccessorSet> accessorSets = new HashMap<>();
	private ConverterFactory converterFactory = new ConverterFactory();

	/**
	 * Create new accessor or find and return existing one. Either factory or targetClass parameters should be set (but not both).
	 * @param sql SQL query for this accessor. Used to distinguish exact accessor if there are many for given factoty or targetClass
	 * @param resultSetColumnTypes map of column names to database specific column type from SQL query metadata. Used when creating new accessor
	 * ORM class members
	 * @param targetClass class of target ORM instances. Can be <code>null></code>. If not <code>null</code> then accessor will scan class and its ancestors
	 * for constructor with appropriate arguments
	 * @param <T> ORM class
	 * @return accessor for ORM class instance initialization
	 */
	@SuppressWarnings( {"unchecked"})
	public <T> Accessor<T> createOrGet(
		CharSequence sql, LinkedHashMap<String,String> resultSetColumnTypes, Supplier<T> factory, Class<T> targetClass
	) throws SQLException {
		AccessorSet accessorSet = accessorSets.computeIfAbsent(targetClass, tClass -> new AccessorSet());
		return  (Accessor<T>) accessorSet.get(sql, resultSetColumnTypes, factory, targetClass, converterFactory);
	}
}

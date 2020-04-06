package ru.resql.orm;

import ru.resql.orm.converters.ConverterFrames;
import ru.resql.orm.stream.SqlDataSource;

import java.sql.SQLException;
import java.util.HashMap;

public class AccessorFactory {
	/**
	 * Aggregate storage for all type accessors
	 * Map of (POJO class) to AccessorKit(Map of (SQL query) to (Accessor)).
	 * This nesting allows to effectively recognize/remove excessively generated Accessors due to dynamic SQL queries.
	 **/
	private final HashMap<Class<?>, AccessorKit<?>> accessorSets = new HashMap<>();

	@SuppressWarnings( {"unchecked"})
	public <T> Accessor<T> createOrGet(
		SqlDataSource sqlDataSource, Class<?> ormClass, ConverterFrames converterFrames
	) throws SQLException {
		@SuppressWarnings("rawtypes") AccessorKit accessorSet = accessorSets.computeIfAbsent(ormClass, tClass -> new AccessorKit());
		return  (Accessor<T>) accessorSet.get(sqlDataSource, ormClass, converterFrames);
	}
}

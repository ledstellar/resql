package ru.resql.orm.converters.todb;

import ru.resql.orm.vendor.postgresql.PgType;

import java.sql.*;
import java.util.Collection;

public class TypedCollection<Type> {
	private final Collection<? extends Type> collection;
	private final Class<Type> itemClass;

	public TypedCollection(Collection<? extends Type> collection, Class<Type> itemClass) {
		this.collection = collection;
		this.itemClass = itemClass;
	}

	public static <Type> TypedCollection<Type> of(Collection<? extends Type> collection, Class<Type> itemClass) {
		return new TypedCollection<>(collection, itemClass);
	}

	public void setStatementParam(PreparedStatement statement, int paramIndex) throws SQLException {

		statement.setArray(
			paramIndex,
			statement.getConnection().createArrayOf(
				PgType.paramJavaClassToDefaultDbType.get(itemClass), collection.toArray()
			)
		);
	}
}

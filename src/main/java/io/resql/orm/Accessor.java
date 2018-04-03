package io.resql.orm;

import java.sql.*;
import java.util.function.Supplier;

public class Accessor<T> {
	Accessor( ResultSetMetaData metaData, Supplier< T > factory, Class< T > targetClass ) {
		// TODO: implement
	}

	/**
	 * Create or get new instance of ORM class and init it with resultset's current record. ORM class instance can be supplied by ORM class supplier
	 * or created by accessor using appropriate class constructor
	 * @param resultSet opened resultset to init ORM class instance
	 * @return initialized ORM class instance
	 */
	T get(ResultSet resultSet) {
		// TODO: implement
		return null;
	}
}

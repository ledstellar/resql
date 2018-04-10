package io.resql.orm;

import java.sql.*;
import java.util.ArrayList;
import java.util.function.Supplier;

class AccessorSet< KeyT, AccessorT > {
	private final AlmostConstantKeyedList< KeyT > keys = new AlmostConstantKeyedList<>();
	private final ArrayList<Accessor<AccessorT>> accessors = new ArrayList<>();

	Accessor<AccessorT> get(KeyT sql, ResultSetMetaData metaData, Supplier<AccessorT> factory, Class<AccessorT> targetClass) throws SQLException {
		int index = keys.indexOf( sql );
		Accessor< AccessorT > accessor;
		if ( index >= accessors.size() ) {
			// this is new key
			accessor = Accessor.newInstance(metaData, factory, targetClass);
			accessors.add( accessor );
		} else {
			accessor = accessors.get( index );
		}
		return accessor;
	}
}

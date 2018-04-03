package io.resql.orm;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.function.Supplier;

public class AccessorSet< KeyT, AccessorT > {
	private final AlmostConstantKeyedList< KeyT > keys = new AlmostConstantKeyedList<>();
	private final ArrayList<Accessor<AccessorT>> accessors = new ArrayList<>();

	Accessor<AccessorT> get(KeyT sql, ResultSetMetaData metaData, Supplier<AccessorT> factory, Class<AccessorT> targetClass) {
		int index = keys.indexOf( sql );
		Accessor< AccessorT > accessor;
		if ( index >= accessors.size() ) {
			// this is new key
			accessor = new Accessor<>(metaData, factory, targetClass);
			accessors.add( accessor );
		} else {
			accessor = accessors.get( index );
		}
		return accessor;
	}
}

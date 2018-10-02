package io.resql.orm;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

class AccessorSet< KeyT, AccessorT > {
	private final AlmostConstantKeyedList< KeyT > keys = new AlmostConstantKeyedList<>();
	private final ArrayList<Accessor<AccessorT>> accessors = new ArrayList<>();

	Accessor<AccessorT> get(KeyT sql, LinkedHashMap<String,Integer> resultSetColumnTypes, Supplier<AccessorT> factory, Class<AccessorT> targetClass, ConverterFactory converterFactory) throws
		SQLException {
		int index = keys.indexOf( sql );
		Accessor< AccessorT > accessor;
		if ( index >= accessors.size() ) {
			// this is new key
			accessor = Accessor.newInstance(resultSetColumnTypes, factory, targetClass, converterFactory);
			accessors.add( accessor );
		} else {
			accessor = accessors.get( index );
		}
		return accessor;
	}
}

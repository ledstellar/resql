package io.resql.orm;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

class AccessorSet<AccessorT> {
	private final HashMap<CharSequence, Accessor> accessors = new HashMap<>();

	Accessor<AccessorT> get(CharSequence sql, LinkedHashMap<String,String> resultSetColumnTypes, Supplier<AccessorT> factory, Class<AccessorT> targetClass,
	                        ConverterFactory converterFactory) throws
		SQLException {
		Accessor accessor = accessors.get(sql);
		if (accessor == null) {
			accessor = Accessor.newInstance(resultSetColumnTypes, factory, targetClass, converterFactory);
			accessors.put(sql, accessor);
		}
		//noinspection unchecked
		return accessor;
	}
}

package ru.resql.orm;

import ru.resql.orm.converters.ConverterFrames;
import ru.resql.orm.stream.SqlDataSource;

import java.sql.SQLException;
import java.util.HashMap;

class AccessorKit<AccessorType> {
	private final HashMap<Object, Accessor<?>> accessors = new HashMap<>();

	Accessor<AccessorType> get(
		SqlDataSource sqlDataSource, Class<AccessorType> targetClass, ConverterFrames converterFrames
	) throws SQLException {
		Object accessorKey = sqlDataSource.getAccessorKey();
		Accessor<?> accessor = accessors.get(accessorKey);
		if (accessor == null) {
			accessor = Accessor.newInstance(sqlDataSource.getLogger(), sqlDataSource.getResultSetMetaData(), targetClass, converterFrames);
			accessors.put(accessorKey, accessor);
		}
		//noinspection unchecked
		return (Accessor<AccessorType>) accessor;
	}
}

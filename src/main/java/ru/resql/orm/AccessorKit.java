package ru.resql.orm;

import ru.resql.orm.converters.ConverterFrames;
import ru.resql.orm.stream.SqlDataSource;

import java.sql.SQLException;
import java.util.HashMap;

class AccessorKit<AccessorType> {
	private final HashMap<CharSequence, Accessor<?>> accessors = new HashMap<>();

	Accessor<AccessorType> get(
		SqlDataSource sqlDataSource, Class<AccessorType> targetClass, ConverterFrames converterFrames
	) throws SQLException {
		String sql = sqlDataSource.getQuery();
		Accessor<?> accessor = accessors.get(sql);
		if (accessor == null) {
			accessor = Accessor.newInstance(sqlDataSource.getLogger(), sqlDataSource.getResultSetMetaData(), targetClass, converterFrames);
			accessors.put(sql, accessor);
		}
		//noinspection unchecked
		return (Accessor<AccessorType>) accessor;
	}
}

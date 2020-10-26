package ru.resql.orm.batch;

import org.slf4j.Logger;
import ru.resql.ConnectionWrapper;
import ru.resql.orm.stream.SqlDataSource;

public class BatchSyncDataSource extends SqlDataSource {
	private Class<?> elementType;

	public BatchSyncDataSource(ConnectionWrapper connectionSource, Logger log, Class<?> elementType) {
		super(connectionSource, log);
		this.elementType = elementType;
	}

	@Override
	public Object getAccessorKey() {
		return elementType;
	}
}

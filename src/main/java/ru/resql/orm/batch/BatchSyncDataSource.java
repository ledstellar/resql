package ru.resql.orm.batch;

import org.slf4j.Logger;
import ru.resql.*;
import ru.resql.orm.annotations.Table;
import ru.resql.orm.stream.SqlDataSource;

import java.sql.*;

public class BatchSyncDataSource extends SqlDataSource {
	private final Class<?> elementType;

	public BatchSyncDataSource(ConnectionWrapper connectionSource, Logger log, Class<?> elementType) {
		super(connectionSource, log);
		this.elementType = elementType;
	}

	@Override
	public Object getAccessorKey() {
		return elementType;
	}

	@Override
	public ResultSetMetaData getResultSetMetaData() throws SQLException {
		Table tableAnnotation = elementType.getAnnotation(Table.class);
		String tableFullName = tableAnnotation.name();
		String schemaName = tableAnnotation.schema();
		if (!"".equals(schemaName)) {
			tableFullName = schemaName + '.' + schemaName;
		}
		try (Connection connection = connectionSource.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableFullName + " LIMIT 0");
			return resultSet.getMetaData();
		}
	}

	@Override
	public boolean isOptionalFields() {
		return true;
	}
}

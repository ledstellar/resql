package ru.resql.orm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Slf4j @TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestSelectsNoPool extends TestSelectsBase<PGSimpleDataSource> {
	TestSelectsNoPool() throws IOException, SQLException {
		super("pgsql_pgjdbc.properties", "pgsql no pool", log);
	}

	@Override
	PGSimpleDataSource getDataSource(Properties properties) throws SQLException {
		PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
		for (Map.Entry<Object, Object> prop : properties.entrySet()) {
			pgSimpleDataSource.setProperty((String)prop.getKey(), (String)prop.getValue());
		}
		return pgSimpleDataSource;
	}
}

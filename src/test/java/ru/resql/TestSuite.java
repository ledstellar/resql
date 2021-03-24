package ru.resql;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.*;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({"ru.resql"})
public class TestSuite {
	public final static String TEST_SCHEMA_NAME = "__resql_test";

	public static void recreateTestSchema(PostgresqlDbPipe pipe) {
		dropTestSchema(pipe);
		pipe.execute("CREATE SCHEMA IF NOT EXISTS " + TEST_SCHEMA_NAME);
	}

	public static void dropTestSchema(PostgresqlDbPipe pipe) {
		pipe.execute("DROP SCHEMA IF EXISTS " + TEST_SCHEMA_NAME + " CASCADE");
	}
}

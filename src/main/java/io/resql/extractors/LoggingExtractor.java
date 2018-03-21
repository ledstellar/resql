package io.resql.extractors;

import org.slf4j.Logger;

import java.sql.*;

@FunctionalInterface
public interface LoggingExtractor< ReturnType > {
	ReturnType extract( ResultSet rs, Logger log ) throws SQLException;
}

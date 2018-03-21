package io.resql.extractors;

import java.sql.*;

@FunctionalInterface
public interface Extractor< ReturnType > {
	ReturnType extract( ResultSet rs ) throws SQLException;
}

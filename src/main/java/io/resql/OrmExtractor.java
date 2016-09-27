package io.resql;

import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.stream.Stream;

@FunctionalInterface
public interface OrmExtractor< OrmType, ReturnType > {
	ReturnType extract(Stream< OrmType  > ormStream, Logger log ) throws SQLException;
}

package ru.resql;

import org.slf4j.*;
import ru.resql.orm.AccessorFactory;
import ru.resql.orm.converters.ConverterFrames;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Connection(s) tied to a particular database.
 */
public abstract class DbManager {
	AccessorFactory accessorFactory;
	ConverterFrames converterFrames;
	final DataSource dataSource;
	final String dataSourceDescription;

	public static final Marker RESQL_SELECT = MarkerFactory.getMarker( "reSql.select" );
	public static final Marker RESQL_UPDATE = MarkerFactory.getMarker( "reSql.update" );
	public static final Marker RESQL_BATCH = MarkerFactory.getMarker( "reSql.batch" );
	public static final Marker RESQL_MAPPINGS = MarkerFactory.getMarker( "reSql.mappings" );
	public static final Marker RESQL_RESULT = MarkerFactory.getMarker( "reSql.result" );
	public static final Marker RESQL_CONNECTION = MarkerFactory.getMarker( "reSql.connection" );
	public static final Marker RESQL_STATS = MarkerFactory.getMarker( "reSql.stats" );

	DbManager(DataSource dataSource, String dataSourceDescription) {
		this.dataSource = dataSource;
		this.dataSourceDescription = dataSourceDescription;
		converterFrames = new ConverterFrames();
		accessorFactory = new AccessorFactory();
	}

	public abstract DbPipe getPipe(Logger log);

	public Connection getConnection(boolean isAutoCommitMode) throws SqlException {
		try {
			Connection connection = dataSource.getConnection();
			if (isAutoCommitMode ^ connection.getAutoCommit()) {
				connection.setAutoCommit(isAutoCommitMode);
			}
			return connection;
		} catch (SQLException sqlException) {
			throw new SqlException("Error acquiring connection"
				+ getConnectionSourceDescription(),
				sqlException
			);
		}
	}

	public String getConnectionSourceDescription() {
		return dataSourceDescription == null ? "" : (" " + dataSourceDescription);
	}

	public AccessorFactory getAccessorFactory() {
		return accessorFactory;
	}

	public ConverterFrames getConverterFrames() {
		return converterFrames;
	}
}

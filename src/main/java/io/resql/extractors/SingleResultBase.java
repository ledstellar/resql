package io.resql.extractors;

import io.resql.SqlException;
import org.slf4j.Logger;

import java.sql.*;

/**
 * Base class for singe result extractors.
 */
abstract class SingleResultBase< ResultType > implements Extractor< ResultType > {
	private boolean isNullAllowed;

	protected SingleResultBase( boolean isNullAllowed ) {
		this.isNullAllowed = isNullAllowed;
	}

	public ResultType extract(ResultSet rs, Logger log) throws SQLException {
		if ( rs.next() ) {
			ResultType result = getTypedResult( rs );
			if ( ! rs.wasNull() ) {
				if ( rs.next() ) {
					throw new SqlException( "Expected single " + getExtractorTypeDescription() + " result but got multiple records" );
				}
				return result;
			}
			if ( isNullAllowed ) {
				return null;
			}
			throw new SqlException( "Expected single " + getExtractorTypeDescription() + " result but got null" );
		}
		if ( isNullAllowed ) {
			return null;
		}
		throw new SqlException( "Expected single " + getExtractorTypeDescription() + " but got no records in resultset" );
	}

	abstract protected ResultType getTypedResult( ResultSet rs) throws SQLException;
	abstract protected String getExtractorTypeDescription();
}

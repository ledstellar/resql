package io.resql.extractors;

import java.sql.*;

public class StringResult extends SingleResultBase< String > {
	public static StringResult single = new StringResult( false );
	public static StringResult singleOrNone = new StringResult( true );

	private StringResult( boolean isNullAllowed ) {
		super( isNullAllowed );
	}

	@Override
	protected String getTypedResult(ResultSet rs) throws SQLException {
		return rs.getString( 1 );
	}

	@Override
	protected String getExtractorTypeDescription() {
		return "string";
	}

	@Override
	public String extract(ResultSet rs) throws SQLException {
		return null; // TODO: implement
	}
}

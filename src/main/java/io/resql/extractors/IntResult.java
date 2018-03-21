package io.resql.extractors;

import java.sql.*;

public class IntResult extends SingleResultBase< Integer > {
	public static IntResult single = new IntResult( false );
	public static IntResult singleOrNone = new IntResult( true );

	private IntResult( boolean isNullAllowed ) {
		super( isNullAllowed );
	}

	@Override
	protected Integer getTypedResult(ResultSet rs) throws SQLException {
		return rs.getInt( 1 );
	}

	@Override
	protected String getExtractorTypeDescription() {
		return "integer";
	}

	@Override
	public Integer extract(ResultSet rs) throws SQLException {
		Integer a;
			return null; // TODO: implement
	}
}

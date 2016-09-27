package io.resql.extractors;

import java.sql.*;

public class DateResult extends SingleResultBase< Date > {
	public static DateResult single = new DateResult( false );
	public static DateResult singleOrNone = new DateResult( true );

	private DateResult( boolean isNullAllowed ) {
		super( isNullAllowed );
	}

	@Override
	protected Date getTypedResult(ResultSet rs) throws SQLException {
		return rs.getDate( 1 );
	}

	@Override
	protected String getExtractorTypeDescription() {
		return "date";
	}
}
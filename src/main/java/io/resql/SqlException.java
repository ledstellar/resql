package io.resql;

public class SqlException extends RuntimeException {
	public SqlException( String message ) {
		super( message );
	}

	public SqlException( String message, Throwable reason ) {
		super( message, reason );
	}
}

package ru.resql;

public class SqlException extends RuntimeException {
	private String debugSql;
	private String resultSetRepresentation;

	public SqlException( String message ) {
		super( message );
	}

	public SqlException( String message, Throwable reason ) {
		super( message, reason );
	}

	public void setRequestSql(String debugSql) {
		this.debugSql = debugSql;
	}

	public void setCurrentResultSet(String resultSetRepresentation) {
		this.resultSetRepresentation = resultSetRepresentation;
	}

	@Override public String getMessage() {
		StringBuilder out = new StringBuilder(super.getMessage());
		out.append("\n").append(debugSql);
		if (resultSetRepresentation != null) {
			out.append("\n--------------------\n").append(resultSetRepresentation).append("--------------------");
		}
		return out.toString();
	}
}

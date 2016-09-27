package io.resql.asmaccesstest;

import java.sql.SQLException;

public interface ClassAccessor {
	void setOrmFields( Object tc ) throws SQLException;
}

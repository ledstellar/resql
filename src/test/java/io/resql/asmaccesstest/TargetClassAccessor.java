package io.resql.asmaccesstest;

import java.sql.SQLException;

public class TargetClassAccessor implements ClassAccessor {
	@Override public void setOrmFields( Object orm ) throws SQLException {
		final TargetClass tc = (TargetClass)orm;
//		tc.privateAccessInt = 1;
		tc.defaultAccessString = "строка";
		tc.publicAccessDouble = 2.;
		tc.protectedMySelf = tc;
	}

}

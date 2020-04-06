package ru.resql.util;

import java.sql.*;

import static java.sql.Types.*;

public class ResultSetDebugFormatter {
	public static String format(ResultSet resultSet) {
		try {
			if (resultSet == null || resultSet.isBeforeFirst() || resultSet.isAfterLast()) {
				return null;
			}
			StringBuilder rs = new StringBuilder("CURRENT RECORD is:\n");
			ResultSetMetaData metaData = resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); ++ i) {
				if (i < 10) {
					rs.append(' ');
				}
				String columnTypeName = metaData.getColumnTypeName(i);
				if (columnTypeName.startsWith("_")) {
					columnTypeName = columnTypeName.substring(1) + "[]";
				}
				rs.append(i)
					.append(": ").append(columnTypeName)
					.append(' ').append(metaData.getColumnName(i))
					.append(": ");
				int columnType = metaData.getColumnType(i);
				Object value = resultSet.getObject(i);
				if (value == null) {
					rs.append("NULL");
				} else if (columnType == TINYINT || columnType == SMALLINT || columnType == INTEGER ||
					columnType == BIGINT || columnType == FLOAT || columnType == REAL ||
					columnType == DOUBLE || columnType == NUMERIC || columnType == DECIMAL ||
					columnType == ARRAY
				) {
					rs.append(resultSet.getString(i));
				} else if (columnType == BOOLEAN || columnType == BIT) {
					rs.append(((Boolean)value) ? "TRUE" : "FALSE");
				} else {
					rs.append('"').append(resultSet.getString(i)).append('"');
				}
				rs.append('\n');
			}
			return rs.toString();
		} catch (SQLException sqle) {
			return "RESULTSET cannot be aquired: " + sqle.getMessage();	// TODO: concat all messages
		}
	}
}

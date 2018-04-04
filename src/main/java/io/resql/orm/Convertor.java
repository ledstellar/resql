package io.resql.orm;

import java.lang.reflect.Field;
import java.sql.*;

public interface Convertor< FieldType > {
	/**
	 * State DB column type this convertor can be applied to
	 * @return SQLType constant of database column type
	 */
	int getDbColumnType();

	/**
	 * State ORM class field type this convertor can be applied to
	 * @return ORM field class
	 */
	Class< FieldType > getFieldType();

	/**
	 * Convert value from resultset to class field value
	 * @param ormInstance instance of class that must be initialized
	 * @param ormField ORM class
	 * @param rs resultset to get the value from
	 * @param columnIndex resultset column index to get the value from
	 */
	void setDbValueToClass( Object ormInstance, Field ormField, ResultSet rs, int columnIndex );

	/**
	 * Convert value from class field value to database query parameter
	 * @param ormInstance instance of class to get the value from
	 * @param ormField the class field to get the value from
	 * @param ps prepared statement to set the value to
	 * @param columnIndex index of parameter to set the value to
	 */
	void setClassValueToDb( Object ormInstance, Field ormField, PreparedStatement ps, int columnIndex );
}

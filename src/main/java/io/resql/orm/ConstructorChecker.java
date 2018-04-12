package io.resql.orm;

import java.lang.reflect.Parameter;
import java.sql.*;

interface ConstructorChecker {
	boolean isConstructorFit(Parameter[] params, ResultSetMetaData metaData, ConvertorFactory convertorFactory) throws SQLException;
	Convertor[] setupConvertors(Parameter[] params, ResultSetMetaData metaData, ConvertorFactory convertorFactory) throws SQLException;
}

package io.resql.orm;

import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.*;

/**
 * This constructor finder is useful when use with implicit field selection and when there can be different matches
 */
class FitByParamTypesStrict implements ConstructorChecker {
	@Override
	public boolean isConstructorFit(Parameter[] params, LinkedHashMap<String, Integer> resultSetColumnTypes, ConvertorFactory convertorFactory) throws SQLException {
		int index = 1;
		for (int columnSqlType : resultSetColumnTypes.values()) {
			if (! convertorFactory.isExists(columnSqlType, params[index].getClass())) {
				return false;
			}
			++ index;
		}
		return true;
	}

	@Override
	public Convertor[] setupConvertors(Parameter[] params, LinkedHashMap<String, Integer> resultSetColumnTypes, ConvertorFactory convertorFactory) throws SQLException {
		return null;    // TODO: implement
	}
}

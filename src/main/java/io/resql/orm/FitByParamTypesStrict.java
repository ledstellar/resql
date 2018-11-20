package io.resql.orm;

import io.resql.orm.converters.*;

import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.*;

/**
 * This constructor finder is useful when use with implicit field selection and when there can be different matches.
 *
 * Non strict parameter types (i.e. parameter with arbitrary type sequence) are disallowed as non safe
 */
class FitByParamTypesStrict implements ConstructorChecker {
	@Override
	public String getDescription() {
		return "by param types strict";
	}

	@Override
	public boolean isConstructorFit(Parameter[] params, LinkedHashMap<String, String> resultSetColumnTypes, ConverterFactory converterFactory) throws SQLException {
		int index = 1;
		for (String columnSqlType : resultSetColumnTypes.values()) {
			if (! converterFactory.isExists(columnSqlType, params[index].getClass())) {
				return false;
			}
			++ index;
		}
		return true;
	}

	@Override
	public FromDbConverter[] setupConvertors(Parameter[] params, LinkedHashMap<String, String> resultSetColumnTypes, ConverterFactory converterFactory) throws SQLException {
		return null;    // TODO: implement
	}
}

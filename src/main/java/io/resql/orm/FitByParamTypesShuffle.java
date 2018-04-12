package io.resql.orm;

import java.lang.reflect.Parameter;
import java.sql.*;

class FitByParamTypesShuffle implements ConstructorChecker {
	@Override
	public boolean isConstructorFit(Parameter[] params, ResultSetMetaData metaData, ConvertorFactory convertorFactory) throws SQLException {
		int index = 1;
		for (Parameter parameter : params) {
			if (!convertorFactory.isExists(metaData.getColumnType(index), parameter.getType())) {
				break;
			}
			++index;
		}
		return true;
	}

	@Override
	public Convertor[] setupConvertors(Parameter[] params, ResultSetMetaData metaData, ConvertorFactory convertorFactory) throws SQLException {
		// TODO: implement
		return null;
	}
}

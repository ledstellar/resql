package io.resql.orm;

import io.resql.util.TypeNames;

import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.*;

class FitByNameConstructorChecker implements ConstructorChecker {
	@Override
	public boolean isConstructorFit(Parameter[] parameters, LinkedHashMap<String,Integer> resultSetColumnTypes, ConvertorFactory convertorFactory) throws SQLException {
		for (Parameter parameter : parameters) {
			if (!parameter.isNamePresent()) {
				return false;
			}
			final String parameterName = parameter.getName();
			boolean columnFound = false;
			for (String columnName : resultSetColumnTypes.keySet()) {
				if (Accessor.isNamesMatch(columnName, parameterName)) {
					// do not check here for ambiguous column name. This check will be made if this constructor will uniquely elected
					columnFound = true;
					break;
				}
			}
			if (!columnFound) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Convertor[] setupConvertors(Parameter[] params, LinkedHashMap<String,Integer> resultSetColumnTypes, ConvertorFactory convertorFactory) throws SQLException {
		ArrayList<String> ambiguous = new ArrayList<>();
		ArrayList<String> ambiguousGroups = new ArrayList<>();
		ArrayList<String> undefinedConvertors = new ArrayList<>();
		Convertor[] paramConvertors = new Convertor[params.length];
		int convertorIndex = 0;
		for (Parameter parameter : params) {
			ambiguous.clear();
			final String parameterName = parameter.getName();
			for (Map.Entry<String,Integer> columnEntry : resultSetColumnTypes.entrySet()) {
				String columnName = columnEntry.getKey();
				if (Accessor.isNamesMatch(columnName, parameterName)) {
					ambiguous.add(columnName);
				}
				if (ambiguous.size() > 1) {
					ambiguousGroups.add(String.join(" and ", ambiguous));
				} else {
					int columnType = columnEntry.getValue();
					Class paramclass = parameter.getClass();
					Convertor convertor = convertorFactory.get(columnType,parameter.getClass());
					if (convertor == null ) {
						undefinedConvertors.add(TypeNames.getAllJdbcTypeNames().get(columnType)+" "+columnName+" -> "+paramclass.getName()+" "+parameterName);
					}
					paramConvertors[convertorIndex] = convertor;
				}
			}
		}
		return paramConvertors;
	}
}

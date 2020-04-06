package ru.resql.orm;

import ru.resql.orm.converters.*;
import ru.resql.orm.converters.instances.*;
import ru.resql.util.TypeNames;

import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Constructor to result set fit checker. This checker is only applied when constructor parameter names are available.
 * To make constructor parameter names available in runtime one should supply -parameters option to the javac compiler
 * when compiling module's source code.
 */
class FitByNameConstructorChecker implements ConstructorChecker {
	@Override
	public String getDescription() {
		return "by name";
	}

	@Override
	public boolean isConstructorFit(Parameter[] parameters, LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes, ConverterFrames converterFrames) {
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
	public FromDbConverter[] setupConvertors(
		Parameter[] params, LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes, ConverterFrames converterFrames
	) {
		ArrayList<String> ambiguous = new ArrayList<>();
		ArrayList<String> ambiguousGroups = new ArrayList<>();
		ArrayList<String> undefinedConvertors = new ArrayList<>();
		FromDbConverter[] paramConvertors = new FromDbConverter[params.length];
		int convertorIndex = 0;
		for (Parameter parameter : params) {
			ambiguous.clear();
			final String parameterName = parameter.getName();
			int columnIndex = 1;
			for (Map.Entry<String,ColumnDescriptor> columnEntry : resultSetColumnTypes.entrySet()) {
				String columnName = columnEntry.getKey();
				if (Accessor.isNamesMatch(columnName, parameterName)) {
					ambiguous.add(columnName);
				}
				if (ambiguous.size() > 1) {
					ambiguousGroups.add(String.join(" and ", ambiguous));
				} else {
					ColumnDescriptor columnDescriptor = columnEntry.getValue();
					Class<?> paramclass = parameter.getClass();
					ConverterFrame converterFrame = null; // FIXME: converterFrames.get(columnType,parameter.getClass());
					if (converterFrame == null ) {
						undefinedConvertors.add(TypeNames.getAllJdbcTypeNames().get(columnDescriptor.columnSqlType)+" "+columnName+" -> "+paramclass.getName()+" "+parameterName);
					}
					paramConvertors[convertorIndex] = null; // FIXME: need another way of creating constructor parameters
				}
			}
		}
		return paramConvertors;
	}
}

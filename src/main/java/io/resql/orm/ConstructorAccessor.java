package io.resql.orm;

import io.resql.SqlException;
import io.resql.util.TypeNames;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

class ConstructorAccessor<T> extends Accessor<T> {
	private Constructor<T> constructor;
	private Convertor[] paramConvertors;

	private interface ConstructorChecker {
		boolean isConstructorFit(Parameter[] params, ResultSetMetaData metaData) throws SQLException;

		void setupConvertors(Parameter[] params, ResultSetMetaData metaData) throws SQLException;
	}

	@SuppressWarnings("unchecked")
	ConstructorAccessor(ResultSetMetaData metaData, Class<T> targetClass) throws SQLException {
		scanForConstructors(metaData, targetClass);
		constructor.setAccessible(true);
	}

	private void scanForConstructors(ResultSetMetaData metaData, Class<T> targetClass) throws SQLException {
		Constructor<?> declaredConstructors[] = targetClass.getDeclaredConstructors();
		if (!scan(declaredConstructors, metaData, "name", new FitByNameConstructorChecker())
			&& !scan(declaredConstructors, metaData, "types strict", new FitByParamTypesStrict())
			&& !scan(declaredConstructors, metaData, "types shuffle", new FitByParamTypesShuffle())) {
			throw new ClassMappingException(
				targetClass,
				"Can't find appropriate constructor in:\n"
					+ String.join("\n", toDescriptions(Arrays.asList(declaredConstructors)))
			);
		}
	}

	private boolean scan(
		Constructor<?>[] declaredConstructors, ResultSetMetaData metaData, String scanTypeDesc, ConstructorChecker constructorChecker
	) throws SQLException {
		ArrayList<Constructor<?>> constructors = new ArrayList<>();
		for (Constructor<?> constructor : declaredConstructors) {
			if (constructor.getParameterCount() > metaData.getColumnCount() // each constructor parameter should be mapped
				&& constructorChecker.isConstructorFit(constructor.getParameters(), metaData)) {
				constructors.add(constructor);
			}
		}
		if (constructors.size() == 0) {
			return false;
		}
		if (constructors.size() > 1) {
			throw new SqlException(
				"Ambigious constructors found while scanning for constructors " + scanTypeDesc + ":\n"
					+ String.join(",\n", toDescriptions(constructors))
			);
		}
		Constructor constructor = constructors.get(0);
		return true;
	}

	private List<String> toDescriptions(Collection<Constructor<?>> constructors) {
		return constructors.stream().map(this::toDescription).collect(Collectors.toList());
	}

	private String toDescription(Constructor<?> constructor) {
		final String PARAM_SEPARATOR = ", ";
		Class<?> declaringClass = constructor.getDeclaringClass();
		StringBuilder builder = new StringBuilder(declaringClass.getPackageName())
			.append(declaringClass.getSimpleName())
			.append(constructor.getName())
			.append('(');
		Parameter[] parameters = constructor.getParameters();
		for (Parameter parameter : parameters) {
			builder.append(parameter.getType().getName());
			if (parameter.isNamePresent()) {
				builder.append(' ').append(parameter.getName());
			}
			builder.append(PARAM_SEPARATOR);
		}
		if (parameters.length > 0) {
			builder.setLength(builder.length() - PARAM_SEPARATOR.length());
		}
		builder.append(')');
		return builder.toString();
	}

	private class FitByNameConstructorChecker implements ConstructorChecker {
		@Override
		public boolean isConstructorFit(Parameter[] parameters, ResultSetMetaData metaData) throws SQLException {
			for (Parameter parameter : parameters) {
				if (!parameter.isNamePresent()) {
					return false;
				}
				final String parameterName = parameter.getName();
				boolean columnFound = false;
				for (int columnIndex = metaData.getColumnCount(); columnIndex > 0; --columnIndex) {
					if (isNamesMatch(metaData.getColumnName(columnIndex), parameterName)) {
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
		public void setupConvertors(Parameter[] params, ResultSetMetaData metaData) throws SQLException {
			ArrayList<String> ambiguous = new ArrayList<>();
			ArrayList<String> ambiguousGroups = new ArrayList<>();
			ArrayList<String> undefinedConvertors = new ArrayList<>();
			paramConvertors = new Convertor[params.length];
			int convertorIndex = 0;
			for (Parameter parameter : params) {
				ambiguous.clear();
				final String parameterName = parameter.getName();
				for (int columnIndex = metaData.getColumnCount(); columnIndex > 0; --columnIndex) {
					String columnName = metaData.getColumnName(columnIndex);
					if (isNamesMatch(columnName, parameterName)) {
						ambiguous.add(columnName);
					}
					if (ambiguous.size() > 1) {
						ambiguousGroups.add(String.join(" and ", ambiguous));
					} else {
						int columnType = metaData.getColumnType(columnIndex);
						Class paramclass = parameter.getClass();
						Convertor convertor = findConvertor(metaData.getColumnType(columnIndex),parameter.getClass());
						if (convertor == null ) {
							undefinedConvertors.add(TypeNames.getAllJdbcTypeNames().get(columnType)+" "+columnName+" -> "+paramclass.getName()+" "+parameterName);
						}
						paramConvertors[convertorIndex] = convertor;
					}
				}
			}
		}
	}

	private class FitByParamTypesShuffle implements ConstructorChecker {
		@Override
		public boolean isConstructorFit(Parameter[] params, ResultSetMetaData metaData) throws SQLException {
			int index = 1;
			for (Parameter parameter : params) {
				if (!isConvertorAvailable(metaData.getColumnType(index), parameter.getType())) {
					break;
				}
				++index;
			}
			return true;
		}

		@Override
		public void setupConvertors(Parameter[] params, ResultSetMetaData metaData) throws SQLException {

		}
	}

	private class FitByParamTypesStrict implements ConstructorChecker {
		@Override
		public boolean isConstructorFit(Parameter[] params, ResultSetMetaData metaData) throws SQLException {
			int index = 1;
			for (Parameter parameter : params) {
				if (!isConvertorAvailable(metaData.getColumnType(index), parameter.getType())) {
					return false;
				}
				++index;
			}
			return true;
		}

		@Override
		public void setupConvertors(Parameter[] params, ResultSetMetaData metaData) throws SQLException {

		}
	}
}

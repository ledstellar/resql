package io.resql.orm;

import io.resql.SqlException;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

class ConstructorAccessor<T> extends Accessor<T> {
	private Constructor<T> constructor;
	private Convertor[] paramConvertors;

	@FunctionalInterface
	private interface ConstructorChecker {
		boolean isConstructorFit(Parameter[] params, ResultSetMetaData metaData) throws SQLException;
	}

	@SuppressWarnings("unchecked")
	ConstructorAccessor(ResultSetMetaData metaData, Class<T> targetClass) throws SQLException {
		constructor = findConstructor(metaData, targetClass);
		constructor.setAccessible(true);
		
	}

	private Constructor findConstructor(ResultSetMetaData metaData, Class<T> targetClass) throws SQLException {
		Constructor<?> declaredConstructors[] = targetClass.getDeclaredConstructors();
		Constructor constructor;
		constructor = scan(declaredConstructors, metaData, "name", this::isParamsFitByName);
		if (constructor == null) {
			constructor = scan(declaredConstructors, metaData, "types strict", this::isParamsFitByParamTypesStrict);
			if (constructor == null) {
				constructor = scan(declaredConstructors, metaData, "types shuffle", this::isParamsFitByParamTypesShuffle);
				if (constructor == null) {
					throw new ClassMappingException(
						targetClass,
						"Can't find appropriate constructor in:\n"
							+ String.join("\n", toDescriptions(Arrays.asList(declaredConstructors)))
					);
				}
			}
		}
		return constructor;
	}

	private Constructor<?> scan(
		Constructor<?>[] declaredConstructors, ResultSetMetaData metaData, String scanTypeDesc, ConstructorChecker constructorChecker
	) throws SQLException {
		ArrayList<Constructor<?>> constructors = new ArrayList<>();
		for (Constructor<?> constructor : declaredConstructors) {
			if (constructor.getParameterCount() > metaData.getColumnCount() // each constructor parameter should be mapped
				&& constructorChecker.isConstructorFit(constructor.getParameters(), metaData)
				) {
				constructors.add(constructor);
			}
		}
		if (constructors.size() == 0) {
			return null;
		}
		if (constructors.size() > 1) {
			throw new SqlException(
				"Ambigious constructors found while scanning for constructors " + scanTypeDesc + ":\n"
					+ String.join(",\n", toDescriptions(constructors))
			);
		}
		return constructors.get(0);
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

	private boolean isParamsFitByName(Parameter[] parameters, ResultSetMetaData metaData) throws SQLException {
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

	private boolean isParamsFitByParamTypesShuffle(Parameter[] parameters, ResultSetMetaData metaData) throws SQLException {
		int index = 1;
		for (Parameter parameter : parameters) {
			if (!isConvertorAvailable(metaData.getColumnType(index), parameter.getType())) {
				break;
			}
			++index;
		}
		return true;
	}

	private boolean isParamsFitByParamTypesStrict(Parameter[] parameters, ResultSetMetaData metaData) throws SQLException {
		int index = 1;
		for (Parameter parameter : parameters) {
			if (!isConvertorAvailable(metaData.getColumnType(index), parameter.getType())) {
				return false;
			}
			++index;
		}
		return true;
	}
}

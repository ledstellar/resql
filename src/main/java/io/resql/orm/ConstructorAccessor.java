package io.resql.orm;

import io.resql.SqlException;
import io.resql.util.TypeNames;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

class ConstructorAccessor<T> extends Accessor<T> {
	private Constructor<T> constructor;
	private Convertor[] paramConvertors;

	private static FitByNameConstructorChecker byNameChecker = new FitByNameConstructorChecker();
	private static FitByParamTypesStrict byTypeStrictChecker = new FitByParamTypesStrict();

	@SuppressWarnings("unchecked")
	ConstructorAccessor(LinkedHashMap<String, Integer> resultSetColumnTypes, Class<T> targetClass, ConvertorFactory convertorFactory) throws SQLException {
		Constructor<?> declaredConstructors[] = targetClass.getDeclaredConstructors();
		if (!scan(declaredConstructors, resultSetColumnTypes, "name", byNameChecker, convertorFactory)
			&& !scan(declaredConstructors, resultSetColumnTypes, "types strict", byTypeStrictChecker, convertorFactory)) {
			throw new ClassMappingException(
				targetClass,
				"Can't find appropriate constructor in:\n"
					+ String.join("\n", toDescriptions(Arrays.asList(declaredConstructors)))
					+ " for result set fields:\n"
					+ toDescription(resultSetColumnTypes)
			);
		}
		constructor.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	private boolean scan(
		Constructor<?>[] declaredConstructors, LinkedHashMap<String, Integer> resultSetColumnTypes,
		String scanTypeDesc, ConstructorChecker constructorChecker,
		ConvertorFactory convertorFactory
	) throws SQLException {
		ArrayList<Constructor<?>> constructors = new ArrayList<>();
		for (Constructor<?> constructor : declaredConstructors) {
			if (constructor.getParameterCount() > resultSetColumnTypes.size() // each constructor parameter should be mapped
				&& constructorChecker.isConstructorFit(constructor.getParameters(), resultSetColumnTypes, convertorFactory)) {
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
		constructor = (Constructor<T>)constructors.get(0);
		paramConvertors = constructorChecker.setupConvertors(constructor.getParameters(),resultSetColumnTypes, convertorFactory);
		return true;
	}

	private String toDescription(LinkedHashMap<String, Integer> sqlFieldTypes) {
		return sqlFieldTypes.entrySet().stream()
			.map( entry -> entry.getKey() + ' ' + TypeNames.getName(entry.getValue()))
			.collect(Collectors.joining(", "));
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

}

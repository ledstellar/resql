package io.resql.orm;

import io.resql.SqlException;
import io.resql.orm.converters.*;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

class ConstructorAccessor<T> extends Accessor<T> {
	private Constructor<T> constructor;
	private FromDbConverter[] fromDbConverters;

	private static FitByNameConstructorChecker byNameChecker = new FitByNameConstructorChecker();
	private static FitByParamTypesStrict byTypeStrictChecker = new FitByParamTypesStrict();

	@SuppressWarnings("unchecked")
	ConstructorAccessor(LinkedHashMap<String, String> resultSetColumnTypes, Class<T> targetClass, ConverterFactory converterFactory) throws SQLException {
		Constructor<?> declaredConstructors[] = targetClass.getDeclaredConstructors();
		if (!scan(declaredConstructors, resultSetColumnTypes, byNameChecker, converterFactory)
			&& !scan(declaredConstructors, resultSetColumnTypes, byTypeStrictChecker, converterFactory)) {
			throw new ClassMappingException(
				targetClass,
				"Can't find appropriate constructor among:\n\t"
					+ String.join("\n\t", toDescriptions(Arrays.asList(declaredConstructors)))
					+ "\nfor result set fields:\n\t"
					+ toDescription(resultSetColumnTypes)
			);
		}
		constructor.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	private boolean scan(
		Constructor<?>[] declaredConstructors, LinkedHashMap<String, String> resultSetColumnTypes,
		ConstructorChecker constructorChecker, ConverterFactory converterFactory
	) throws SQLException {
		ArrayList<Constructor<?>> constructors = new ArrayList<>();
		for (Constructor<?> constructor : declaredConstructors) {
			if (constructor.getParameterCount() >= resultSetColumnTypes.size() // each constructor parameter should be mapped
				&& constructorChecker.isConstructorFit(constructor.getParameters(), resultSetColumnTypes, converterFactory)) {
				// collect all apropriateconstructors
				constructors.add(constructor);
			}
		}
		if (constructors.size() == 0) {
			// no constructors fit with given check type
			return false;
		}
		if (constructors.size() > 1) {
			throw new SqlException(
				"Ambiguous constructors found while scanning for constructors " + constructorChecker.getDescription() + ":\n"
					+ String.join(",\n", toDescriptions(constructors))
			);
		}
		constructor = (Constructor<T>)constructors.get(0);
		fromDbConverters = constructorChecker.setupConvertors(constructor.getParameters(),resultSetColumnTypes, converterFactory);
		return true;
	}

	private String toDescription(LinkedHashMap<String, String> sqlFieldTypes) {
		return sqlFieldTypes.entrySet().stream()
			.map( entry -> entry.getKey() + ' ' + entry.getValue())
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

	@Override
	public T get(ResultSet resultSet) {
		return null;
	}
}

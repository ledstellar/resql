package ru.resql.orm;

import ru.resql.SqlException;
import ru.resql.orm.converters.*;
import ru.resql.orm.converters.instances.ColumnDescriptor;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

class ConstructorAccessor<ElementType> extends Accessor<ElementType> {
	private Constructor<ElementType> constructor;

	private final static FitByNameConstructorChecker byNameChecker = new FitByNameConstructorChecker();
	private final static FitByParamTypesStrict byTypeStrictChecker = new FitByParamTypesStrict();

	ConstructorAccessor(Constructor<ElementType> constructor) {
		this.constructor = constructor;
	}

	ConstructorAccessor<ElementType> find(
		LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes,
		Class<ElementType> targetClass, ConverterFrames converterFrames
	) throws SQLException {
		Constructor<?>[] declaredConstructors = targetClass.getDeclaredConstructors();
		Constructor<ElementType> constructor = scan(declaredConstructors, resultSetColumnTypes, byNameChecker, converterFrames);
		if (constructor == null) {
			constructor = scan(declaredConstructors, resultSetColumnTypes, byTypeStrictChecker, converterFrames);
		}
		if (constructor != null) {
			constructor.setAccessible(true);
			return new ConstructorAccessor<>(constructor);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <Type> Constructor<Type> scan(
		Constructor<?>[] declaredConstructors, LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes,
		ConstructorChecker constructorChecker, ConverterFrames converterFrames
	) throws SQLException {
		ArrayList<Constructor<?>> constructors = new ArrayList<>();
		for (Constructor<?> constructor : declaredConstructors) {
			if (constructor.getParameterCount() >= resultSetColumnTypes.size() // each constructor parameter should be mapped
				&& constructorChecker.isConstructorFit(constructor.getParameters(), resultSetColumnTypes, converterFrames)) {
				// collect all apropriateconstructors
				constructors.add(constructor);
			}
		}
		if (constructors.size() == 0) {
			// no constructors fit with given check type
			return null;
		}
		if (constructors.size() > 1) {
			throw new SqlException(
				"Ambiguous constructors found while scanning for constructors " + constructorChecker.getDescription() + ":\n"
					+ String.join(",\n", toDescriptions(constructors))
			);
		}
		return (Constructor<Type>)constructors.get(0);
	}

	private String toDescription(LinkedHashMap<String, ColumnDescriptor> sqlFieldTypes) {
		return sqlFieldTypes.entrySet().stream()
			.map( entry -> entry.getKey() + ' ' + entry.getValue().columnDbTypeName)
			.collect(Collectors.joining(", "));
	}

	private static List<String> toDescriptions(Collection<Constructor<?>> constructors) {
		return constructors.stream().map(ConstructorAccessor::toDescription).collect(Collectors.toList());
	}

	private static String toDescription(Constructor<?> constructor) {
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
	public void init(ElementType element, ResultSet resultSet) {
		// TODO: implement
	}
}

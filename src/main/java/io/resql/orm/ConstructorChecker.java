package io.resql.orm;

import io.resql.orm.converters.Converter;

import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.*;

interface ConstructorChecker {
	boolean isConstructorFit(Parameter[] params, LinkedHashMap<String, Integer> resultSetColumnTypes, ConverterFactory converterFactory) throws SQLException;
	Converter[] setupConvertors(Parameter[] params, LinkedHashMap<String, Integer> resultSetColumnTypes, ConverterFactory converterFactory) throws SQLException;
	String getDescription();
}

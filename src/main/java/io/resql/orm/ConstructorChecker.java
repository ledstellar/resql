package io.resql.orm;

import io.resql.orm.converters.*;

import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.*;

interface ConstructorChecker {
	boolean isConstructorFit(Parameter[] params, LinkedHashMap<String, String> resultSetColumnTypes, ConverterFactory converterFactory) throws SQLException;
	FromDbConverter[] setupConvertors(Parameter[] params, LinkedHashMap<String, String> resultSetColumnTypes, ConverterFactory converterFactory) throws SQLException;
	String getDescription();
}

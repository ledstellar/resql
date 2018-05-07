package io.resql.orm;

import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.*;

interface ConstructorChecker {
	boolean isConstructorFit(Parameter[] params, LinkedHashMap<String, Integer> resultSetColumnTypes, ConvertorFactory convertorFactory) throws SQLException;
	Convertor[] setupConvertors(Parameter[] params, LinkedHashMap<String, Integer> resultSetColumnTypes, ConvertorFactory convertorFactory) throws SQLException;
}

package ru.resql.orm;

import ru.resql.orm.converters.*;
import ru.resql.orm.converters.instances.ColumnDescriptor;

import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.*;

interface ConstructorChecker {
	boolean isConstructorFit(Parameter[] params, LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes, ConverterFrames converterFrames) throws SQLException;
	FromDbConverter[] setupConvertors(Parameter[] params, LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes, ConverterFrames converterFrames) throws SQLException;
	String getDescription();
}

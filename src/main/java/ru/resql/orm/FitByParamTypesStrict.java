package ru.resql.orm;

import ru.resql.orm.converters.*;
import ru.resql.orm.converters.instances.ColumnDescriptor;

import java.lang.reflect.Parameter;
import java.util.*;

/**
 * This constructor finder is useful when use with implicit field selection and when there can be different matches.
 *
 * Non strict parameter types (i.e. parameter with arbitrary type sequence) are disallowed as non safe
 */
class FitByParamTypesStrict implements ConstructorChecker {
	@Override
	public String getDescription() {
		return "by param types strict";
	}

	@Override
	public boolean isConstructorFit(Parameter[] params, LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes, ConverterFrames converterFrames) {
		int index = 1;
		for (ColumnDescriptor columnDescriptor : resultSetColumnTypes.values()) {
//			FIXME: if (! converterFrames.isExists(columnSqlType, params[index].getClass())) {
				return false;
//			}
//			++ index;
		}
		return true;
	}

	@Override
	public FromDbConverter[] setupConvertors(Parameter[] params, LinkedHashMap<String, ColumnDescriptor> resultSetColumnTypes, ConverterFrames converterFrames) {
		return null;    // TODO: implement
	}
}

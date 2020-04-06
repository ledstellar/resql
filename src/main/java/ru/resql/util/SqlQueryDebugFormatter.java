package ru.resql.util;

import ru.resql.PostgresqlDbPipe;
import ru.resql.orm.Tagged;
import ru.resql.orm.stream.ObjectSqlStreamChain;
import ru.resql.orm.vendor.postgresql.PgType;

/**
 * TODO: should be cached as template for inserting ever changing params and probable with caller point
 */
public class SqlQueryDebugFormatter {
	private final String sql;
	private int[] paramIndexes;
	private static final int PROBABLE_AVERAGE_PARAM_SIZE = 10;
	private int lastNonBlancSqlSymbol;

	private SqlQueryDebugFormatter(String sql, int paramCount) {
		this.sql = sql;
		findParamPlaceholders(paramCount);
		findLastNonBlancSymbolIndex();
	}

	public static String getDebugRepresentation(String sql, Object[] params) {
		// If no caller line given then find out it from this point
		if (params == null || params.length == 0) {
			return getCallerSrcLine() + sql;
		}
		// Create object only in case with have to parse query
		SqlQueryDebugFormatter formatter = new SqlQueryDebugFormatter(sql, params.length);
		return getCallerSrcLine() + formatter.insertParams(params);
	}

	public String insertParams(Object[] params) {
		StringBuilder dSql = new StringBuilder(sql.length() + params.length * PROBABLE_AVERAGE_PARAM_SIZE);
		int fragmentStart = 0;
		for (int i = 0; i < params.length; ++ i) {
			dSql.append(sql, fragmentStart, paramIndexes[i]);
			dSql.append("/* ").append(i + 1).append(" */ ");
			appendParam(dSql, params[i]);
			fragmentStart = paramIndexes[i] + 1;	// bypass '?' sign
		}
		dSql.append(sql, fragmentStart, lastNonBlancSqlSymbol);
		return dSql.toString();
	}

	private void appendParam(StringBuilder dSql, Object paramValue) {
		if (paramValue == null) {
			dSql.append("NULL");
		} else {
			Class<?> paramClass = paramValue.getClass();
			if (paramClass.isEnum()) {
				if (Tagged.class.isAssignableFrom(paramClass)) {
					dSql.append(((Tagged<?>)paramValue).getTag().toString())
						.append(" /* ").append(paramClass.getSimpleName()).append('.')
						.append(((Enum<?>)paramValue).name()).append(" */");
				} else {
					dSql.append("'").append(((Enum<?>)paramValue).name()).append("'")
						.append(" /* ").append(paramClass.getSimpleName()).append(" */");
				}
			} else if (paramClass.isArray()) {
				dSql.append("ARRAY[");
				Class<?> componentType = paramClass.getComponentType();
				Object[] boxedArray = toObjectArray(paramValue, componentType);
				for (Object paramValueComponent : boxedArray) {
					appendParam(dSql, paramValueComponent);
					dSql.append(", ");
				}
				dSql.setLength(dSql.length() - ", ".length());
				Class<?> boxedComponentClass = PrimitiveUtil.primitiveToObjects.get(componentType);
				dSql.append("]::").append(
					PgType.paramJavaClassToDefaultDbType.get(boxedComponentClass == null ? componentType : boxedComponentClass)
				).append("[]");
			} else if (String.class.isAssignableFrom(paramClass)) {
				dSql.append("'").append(paramValue.toString()).append("'");
			} else {
				dSql.append(paramValue.toString());
			}
		}
	}

	private Object[] toObjectArray(Object paramValue, Class<?> componentType) {
		Object[] boxedArray;
		if (int.class.isAssignableFrom(componentType)) {
			int[] intArr = (int[])paramValue;
			boxedArray = new Object[intArr.length];
			for (int i = 0; i < intArr.length; ++ i) {
				boxedArray[i] = intArr[i];
			}
		} else if (long.class.isAssignableFrom(componentType)) {
			long[] intArr = (long[])paramValue;
			boxedArray = new Object[intArr.length];
			for (int i = 0; i < intArr.length; ++ i) {
				boxedArray[i] = intArr[i];
			}
		} else if (boolean.class.isAssignableFrom(componentType)) {
			boolean[] intArr = (boolean[])paramValue;
			boxedArray = new Object[intArr.length];
			for (int i = 0; i < intArr.length; ++ i) {
				boxedArray[i] = intArr[i];
			}
		} else if (double.class.isAssignableFrom(componentType)) {
			double[] intArr = (double[])paramValue;
			boxedArray = new Object[intArr.length];
			for (int i = 0; i < intArr.length; ++ i) {
				boxedArray[i] = intArr[i];
			}
		} else if (float.class.isAssignableFrom(componentType)) {
			float[] intArr = (float[])paramValue;
			boxedArray = new Object[intArr.length];
			for (int i = 0; i < intArr.length; ++ i) {
				boxedArray[i] = intArr[i];
			}
		} else {
			boxedArray = (Object[])paramValue;
		}
		return boxedArray;
	}

	private void findParamPlaceholders(int paramCount) {
		Character substringSeparator = null;
		boolean isAfterScreener = false;
		int sqlLength = sql.length();
		int currentParamIndex = 0;

		paramIndexes = new int[paramCount];
		for (int i = 0; i < sqlLength; ++ i) {
			final char ch = sql.charAt(i);
			if (substringSeparator == null) {
				if (ch == '\'' || ch == '"') {
					substringSeparator = ch;
				} else if (ch == '?') {
					paramIndexes[currentParamIndex++] = i;
				}
			} else {
				if (isAfterScreener) {
					isAfterScreener = false;
				} else {
					if (ch == '\\') {
						isAfterScreener = true;
					} else if (ch == substringSeparator) {
						substringSeparator = null;
					}
				}
			}
		}
	}

	private void findLastNonBlancSymbolIndex() {
		for (int i = sql.length() - 1; i >= 0; -- i) {
			char ch = sql.charAt(i);
			if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
				lastNonBlancSqlSymbol = i + 1;
				break;
			}
		}
	}

	public static String getCallerSrcLine() {
		final boolean[] isLastLibraryFrameFound = new boolean[1];
		StackWalker.StackFrame frame = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(
			s -> s.filter(
				f -> {
					Class<?> frameClass = f.getDeclaringClass();
					boolean foundKeyFrameAtThisPass =
						PostgresqlDbPipe.class.equals(frameClass)
						|| ObjectSqlStreamChain.class.equals(frameClass);
					boolean foundKeyFrameAtPreviousPass = isLastLibraryFrameFound[0];
					isLastLibraryFrameFound[0] |= foundKeyFrameAtThisPass;
					return foundKeyFrameAtPreviousPass && !foundKeyFrameAtThisPass;
				})
				.limit(1)
				.findAny()
				.orElse(null));
		if (frame == null) {
			return "";
		}
		return "\n\tat " + frame.getClassName() + "." + frame.getMethodName()
			+ "(" + frame.getFileName() + ':' + frame.getLineNumber() + ")\n";
	}
}

package ru.resql.orm.collators;

import java.util.Collection;

@FunctionalInterface
public interface ColumnCollator {
	/**
	 * Find match of db column names to one and only one field name
	 * @param columnName column name. Can be case (in)sensitive
	 * @param fieldNames class field names
	 * @return one and only one field name from given list
	 * @throws CollationException if the only field can not be choose (field names are ambiguous)
	 */
	String collate(String columnName, Collection<String> fieldNames) throws CollationException;
}

package ru.resql.orm.collators;

import java.util.*;

/**
 * Allow possible underscore to camel case conversions.
 * That is column names 'the_name', 'theName' and 'thename' are all collate to field named 'theField'
 */
@SuppressWarnings("unused")
public class PossibleCamelCaseCollator implements ColumnCollator {
	@Override
	public String collate(String columnName, Collection<String> fieldNames) throws CollationException {
		String lowerColumnName = columnName.toLowerCase();
		StringBuilder preparedColumnName = new StringBuilder(columnName.length());
		boolean nextToUpper = false;
		boolean isUnderscoreWasUsed = false;
		if (columnName.toLowerCase().startsWith("id_")) {
			// specific naming conversion
			columnName = columnName.substring("id_".length()) + "_id";
		}
		for (char ch : columnName.toCharArray()) {
			if (ch == '_') {
				nextToUpper = true;
				isUnderscoreWasUsed = true;
			} else {
				preparedColumnName.append(nextToUpper ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
				nextToUpper = false;
			}
		}
		String foundedField = null;
		String preparedColumnNameStr = preparedColumnName.toString();
		for (String fieldName : fieldNames) {
			if (preparedColumnNameStr.equals(fieldName)
			 || ! isUnderscoreWasUsed && lowerColumnName.equals(fieldName.toLowerCase())) {
				if (foundedField != null) {
					throw new CollationException(
						"Ambiguous fields with name '" + fieldName + "' "
						+ ( fieldName.equals(foundedField) ? "" : " and ' " + foundedField + "' ")
						+ "have found for column named '" + columnName
					);
				} else {
					foundedField = fieldName;
				}
			}
		}
		return foundedField;
	}
}

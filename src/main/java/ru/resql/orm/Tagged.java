package ru.resql.orm;

/** An interface for enums that can be represented in database as integer constants */
@FunctionalInterface
public interface Tagged<TagType> {
	TagType getTag();
	default Class<?> getTagClass() {
		try {
			return getClass().getMethod("getTag").getReturnType();
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException("Internal error: tagged interface has no getTag method", nsme);
		}
	}
}

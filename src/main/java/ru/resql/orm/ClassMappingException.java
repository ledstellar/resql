package ru.resql.orm;

public class ClassMappingException extends RuntimeException {
	private final Class<?> ormClass;

	public ClassMappingException( Class<?> ormClass, String message ) {
		super(message);
		this.ormClass = ormClass;
	}
}

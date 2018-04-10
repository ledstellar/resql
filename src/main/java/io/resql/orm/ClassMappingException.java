package io.resql.orm;

public class ClassMappingException extends RuntimeException {
	private final Class<?> ormClass;

	ClassMappingException( Class<?> ormClass, String message ) {
		super(message);
		this.ormClass = ormClass;
	}
}

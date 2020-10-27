package ru.resql.orm.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(value=TYPE)
@Retention(value=RUNTIME)
public @interface Table {
	/**
	 * (Optional) The name of the table.
	 * <p> Defaults to the entity name.
	 */
	String name() default "";

	/** (Optional) The schema of the table.
	 * <p> Defaults to the default schema for user.
	 */
	String schema() default "";
}
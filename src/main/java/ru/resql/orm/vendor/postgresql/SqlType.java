package ru.resql.orm.vendor.postgresql;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface SqlType {
	PgSqlType value();
}

package ru.resql;

import ru.resql.orm.stream.OrmSqlStreamRecordSource;

import java.sql.ResultSet;
import java.util.function.Supplier;

@FunctionalInterface
public interface StreamMapper<ReturnType>  {
	OrmSqlStreamRecordSource<ReturnType> process(Supplier<ReturnType> factory, ResultSet resultSet);
}

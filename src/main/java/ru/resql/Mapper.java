package ru.resql;

import java.sql.ResultSet;
import java.util.function.Supplier;

@FunctionalInterface
public interface Mapper<ReturnType> {
	ReturnType process(Supplier<ReturnType> factory, ResultSet resultSet);
}

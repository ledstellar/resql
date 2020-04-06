package ru.resql.orm.stream;

import java.util.OptionalInt;

@FunctionalInterface
public interface IntTerminalOperation {
	OptionalInt evaluate();
}

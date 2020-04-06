package ru.resql.orm.stream;

import java.lang.annotation.ElementType;

@FunctionalInterface
public interface TerminalOperation<ReturnType> {
	ReturnType evaluate();
}

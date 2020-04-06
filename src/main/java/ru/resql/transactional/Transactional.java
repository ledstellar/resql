package ru.resql.transactional;

@FunctionalInterface
public interface Transactional {
	void run(TransactionalPipe pipe) throws Throwable;
}

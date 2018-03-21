package io.resql;

@FunctionalInterface
public interface Processor<T,ResultT> {
	ResultT process(ResultSetSupplier<T> resultSet);
}

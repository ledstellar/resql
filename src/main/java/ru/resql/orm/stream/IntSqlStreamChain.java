package ru.resql.orm.stream;

import ru.resql.SqlException;
import ru.resql.util.ResultSetDebugFormatter;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class IntSqlStreamChain implements IntStream {
	/** Data (element) source for this stream chain or <code>null</code> if this chain is the root element source */
	IntSqlStreamChain source;

	SelectWithParamSqlDataSource getRootSqlDataSource() {
		IntSqlStreamChain source = this;
		do {
			IntSqlStreamChain prevSource = source.source;
			if (prevSource == null) {
				return ((IntSqlStreamRecordSource)source).sqlDataSource;
			}
			source = prevSource;
		} while(true);
	}

	abstract boolean tryAdvance(IntConsumer action) throws SqlException;

	public IntStream filter(IntPredicate predicate) {
		// TODO: implement
		throw new RuntimeException("filter is not implemented yet");
	}

	@Override
	public IntStream map(IntUnaryOperator mapper) {
		// TODO: implement
		throw new RuntimeException("map is not implemented yet");
	}

	@Override
	public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
		// TODO: implement
		throw new RuntimeException("mapToObj is not implemented yet");
	}

	@Override
	public LongStream mapToLong(IntToLongFunction mapper) {
		// TODO: implement
		throw new RuntimeException("mapToLong is not implemented yet");
	}

	@Override
	public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
		// TODO: implement
		throw new RuntimeException("mapToDouble is not implemented yet");
	}

	@Override
	public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
		// TODO: implement
		throw new RuntimeException("flatMap is not implemented yet");
	}

	@Override
	public IntStream distinct() {
		// TODO: implement
		throw new RuntimeException("distinct is not implemented yet");
	}

	@Override
	public IntStream sorted() {
		// TODO: implement
		throw new RuntimeException("sorted is not implemented yet");
	}

	@Override
	public IntStream peek(IntConsumer action) {
		// TODO: implement
		throw new RuntimeException("peek is not implemented yet");
	}

	@Override
	public IntStream limit(long maxSize) {
		// TODO: implement
		throw new RuntimeException("limit is not implemented yet");
	}

	@Override
	public IntStream skip(long n) {
		// TODO: implement
		throw new RuntimeException("skip is not implemented yet");
	}

	@Override
	public void forEach(IntConsumer action) {
		// TODO: implement
		throw new RuntimeException("forEach is not implemented yet");
	}

	@Override
	public void forEachOrdered(IntConsumer action) {
		// TODO: implement
		throw new RuntimeException("forEachOrdered is not implemented yet");
	}

	@Override
	public int[] toArray() {
		// TODO: implement
		throw new RuntimeException("toArray is not implemented yet");
	}

	@Override
	public int reduce(int identity, IntBinaryOperator op) {
		// TODO: implement
		throw new RuntimeException("reduce is not implemented yet");
	}

	@Override
	public OptionalInt reduce(IntBinaryOperator op) {
		// TODO: implement
		throw new RuntimeException("reduce is not implemented yet");
	}

	@Override
	public <ResultType> ResultType collect(Supplier<ResultType> supplier, ObjIntConsumer<ResultType> accumulator, BiConsumer<ResultType, ResultType> combiner) {
		// TODO: implement
		throw new RuntimeException("collect is not implemented yet");
	}

	@Override
	public int sum() {
		// TODO: implement
		throw new RuntimeException("sum is not implemented yet");
	}

	@Override
	public OptionalInt min() {
		// TODO: implement
		throw new RuntimeException("min is not implemented yet");
	}

	@Override
	public OptionalInt max() {
		// TODO: implement
		throw new RuntimeException("max is not implemented yet");
	}

	@Override
	public long count() {
		// TODO: implement
		throw new RuntimeException("count is not implemented yet");
	}

	@Override
	public OptionalDouble average() {
		// TODO: implement
		throw new RuntimeException("average is not implemented yet");
	}

	@Override
	public IntSummaryStatistics summaryStatistics() {
		// TODO: implement
		throw new RuntimeException("summaryStatistics is not implemented yet");
	}

	@Override
	public boolean anyMatch(IntPredicate predicate) {
		// TODO: implement
		throw new RuntimeException("anyMatch is not implemented yet");
	}

	@Override
	public boolean allMatch(IntPredicate predicate) {
		// TODO: implement
		throw new RuntimeException("allMatch is not implemented yet");
	}

	@Override
	public boolean noneMatch(IntPredicate predicate) {
		// TODO: implement
		throw new RuntimeException("noneMatch is not implemented yet");
	}

	OptionalInt evaluate(IntTerminalOperation operation) {
		try {
			return operation.evaluate();
		} catch (SqlException sqlException) {
			SelectWithParamSqlDataSource sqlDataSource = getRootSqlDataSource();
			sqlException.setRequestSql(sqlDataSource.getQueryDebug());
			sqlException.setCurrentResultSet(ResultSetDebugFormatter.format(sqlDataSource.resultSet));
			throw sqlException;
		} finally {
			close();
		}
	}

	@Override
	public OptionalInt findFirst() {
		SingleIntConsumer singleIntConsumer = new SingleIntConsumer();
		return evaluate(() -> tryAdvance(singleIntConsumer)
			? OptionalInt.of(singleIntConsumer.element)
			: OptionalInt.empty()
		);
	}

	@Override
	public OptionalInt findAny() {
		return findFirst();
	}

	@Override
	public LongStream asLongStream() {
		// TODO: implement
		throw new RuntimeException("asLongStream is not implemented yet");
	}

	@Override
	public DoubleStream asDoubleStream() {
		// TODO: implement
		throw new RuntimeException("asDoubleStream is not implemented yet");
	}

	@Override
	public Stream<Integer> boxed() {
		// TODO: implement
		throw new RuntimeException("boxed is not implemented yet");
	}

	@Override
	public IntStream sequential() {
		// TODO: implement
		throw new RuntimeException("sequential is not implemented yet");
	}

	@Override
	public IntStream parallel() {
		// TODO: implement
		throw new RuntimeException("parallel is not implemented yet");
	}

	@Override
	public PrimitiveIterator.OfInt iterator() {
		// TODO: implement
		throw new RuntimeException("iterator is not implemented yet");
	}

	@Override
	public Spliterator.OfInt spliterator() {
		// TODO: implement
		throw new RuntimeException("spliterator is not implemented yet");
	}

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public IntStream unordered() {
		// TODO: implement
		throw new RuntimeException("unordered is not implemented yet");
	}

	@Override
	public IntStream onClose(Runnable closeHandler) {
		// TODO: implement
		throw new RuntimeException("onClose is not implemented yet");
	}

	@Override
	public void close() {
		source.close();
	}
}


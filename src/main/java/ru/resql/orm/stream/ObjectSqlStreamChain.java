package ru.resql.orm.stream;

import ru.resql.*;
import ru.resql.util.ResultSetDebugFormatter;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class ObjectSqlStreamChain<ElementType> implements Stream<ElementType> {
	/** Data (element) source for this stream chain or <code>null</code> if this chain is the root element source */
	ObjectSqlStreamChain<ElementType> source;

	ObjectSqlStreamChain(ObjectSqlStreamChain<ElementType> source) {
		this.source = source;
	}

	SqlDataSource getRootSqlDataSource() {
		ObjectSqlStreamChain<?> source = this;
		do {
			ObjectSqlStreamChain<?> prevSource = source.source;
			if (prevSource == null) {
				return ((SqlDataSourceKeeper<?>)source).getSqlDataSource();
			}
			source = prevSource;
		} while(true);
	}

	/**
	 * Trying to get new stream  element from previous chain link and proceed it with action if succeed
	 * @param action action to process under received stream element
	 * @return <code>true</code> if elelement received and passed to action,
	 * <code>false</code> if there no more elements in stream
	 * @throws SqlException when exception was thrown while getting/processing element at previous chains
	 */
	abstract boolean tryAdvance(Consumer<? super ElementType> action) throws SqlException;

	@Override
	public Stream<ElementType> filter(Predicate<? super ElementType> predicate) {
		return new FilterChain<>(this, predicate);
	}

	@Override
	public <R> Stream<R> map(Function<? super ElementType, ? extends R> mapper) {
		// TODO: implement
		throw new RuntimeException("Mapping is not implemented yet");
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super ElementType> mapper) {
		// TODO: implement
		throw new RuntimeException("mapToInt is not implemented yet");
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super ElementType> mapper) {
		// TODO: implement
		throw new RuntimeException("mapToLong is not implemented yet");
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super ElementType> mapper) {
		// TODO: implement
		throw new RuntimeException("mapToDouble is not implemented yet");
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super ElementType, ? extends Stream<? extends R>> mapper) {
		// TODO: implement
		throw new RuntimeException("flatMap is not implemented yet");
	}

	@Override
	public IntStream flatMapToInt(Function<? super ElementType, ? extends IntStream> mapper) {
		// TODO: implement
		throw new RuntimeException("flatMapToInt is not implemented yet");
	}

	@Override
	public LongStream flatMapToLong(Function<? super ElementType, ? extends LongStream> mapper) {
		// TODO: implement
		throw new RuntimeException("flatMapToLong is not implemented yet");
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super ElementType, ? extends DoubleStream> mapper) {
		// TODO: implement
		throw new RuntimeException("flatMapToDouble is not implemented yet");
	}

	@Override
	public Stream<ElementType> distinct() {
		// TODO: implement
		throw new RuntimeException("Distinct is not implemented yet");
	}

	@Override
	public Stream<ElementType> sorted() {
		// TODO: implement
		throw new RuntimeException("Sorting is not implemented yet");
	}

	@Override
	public Stream<ElementType> sorted(Comparator<? super ElementType> comparator) {
		// TODO: implement
		throw new RuntimeException("Sorting is not implemented yet");
	}

	@Override
	public Stream<ElementType> peek(Consumer<? super ElementType> action) {
		return new PeekChain<>(this, action);
	}

	@Override
	public Stream<ElementType> limit(long maxSize) {
		return new LimitChain<>(this, maxSize);
	}

	@Override
	public Stream<ElementType> skip(long n) {
		return new SkipChain<>(this, n);
	}

	@Override
	public void forEach(Consumer<? super ElementType> action) {
		execute(() -> {
			//noinspection StatementWithEmptyBody
			while (tryAdvance(action)) {
				// nothing to do
			}
		});
	}

	@Override
	public void forEachOrdered(Consumer<? super ElementType> action) {
		// TODO: implement
		throw new RuntimeException("forEachOrdered is not implemented yet");
	}

	@Override
	public Object[] toArray() {
		// TODO: implement
		throw new RuntimeException("toArray is not implemented yet");
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		// TODO: implement
		throw new RuntimeException("toArray is not implemented yet");
	}

	@Override
	public ElementType reduce(ElementType identity, BinaryOperator<ElementType> accumulator) {
		// TODO: implement
		throw new RuntimeException("reducing is not implemented yet");
	}

	@Override
	public Optional<ElementType> reduce(BinaryOperator<ElementType> accumulator) {
		// TODO: implement
		throw new RuntimeException("reducing is not implemented yet");
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super ElementType, U> accumulator, BinaryOperator<U> combiner) {
		// TODO: implement
		throw new RuntimeException("reducing is not implemented yet");
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super ElementType> accumulator, BiConsumer<R, R> combiner) {
		// TODO: implement
		throw new RuntimeException("custom collector is not implemented yet");
	}

	@Override
	public <ReturnType, AccumulatorType> ReturnType collect(Collector<? super ElementType, AccumulatorType, ReturnType> collector) {
		return evaluate(() -> {
			AccumulatorType accumulator = Objects.requireNonNull(collector).supplier().get();
			BiConsumer<AccumulatorType, ? super ElementType> addOperation = collector.accumulator();
			//noinspection StatementWithEmptyBody
			while (tryAdvance(elementType -> addOperation.accept(accumulator, elementType))) {
				// nothing to do
			}
			return collector.finisher().apply(accumulator);
		});
	}

	void execute(VoidTerminalOperation operation) {
		try {
			operation.evaluate();
		} catch (SqlException sqlException) {
			SqlDataSource sqlDataSource = getRootSqlDataSource();
			sqlException.setRequestSql(sqlDataSource.getQueryDebug());
			sqlException.setCurrentResultSet(ResultSetDebugFormatter.format(sqlDataSource.resultSet));
			throw sqlException;
		} finally {
			close();
		}
	}

	<ReturnType> ReturnType evaluate(TerminalOperation<ReturnType> operation) {
		try {
			return operation.evaluate();
		} catch (SqlException sqlException) {
			SqlDataSource sqlDataSource = getRootSqlDataSource();
			sqlException.setRequestSql(sqlDataSource.getQueryDebug());
			sqlException.setCurrentResultSet(ResultSetDebugFormatter.format(sqlDataSource.resultSet));
			throw sqlException;
		} finally {
			close();
		}
	}

	@Override
	public Optional<ElementType> min(Comparator<? super ElementType> comparator) {
		// TODO: implement
		throw new RuntimeException("min is not implemented yet");
	}

	@Override
	public Optional<ElementType> max(Comparator<? super ElementType> comparator) {
		// TODO: implement
		throw new RuntimeException("max is not implemented yet");
	}

	@Override
	public long count() {
		return evaluate(() -> {
			long i = 0;
			while (tryAdvance(elementType -> { /* just skip */ })) {
				++ i;
			}
			return i;
		});
	}

	@Override
	public boolean anyMatch(Predicate<? super ElementType> predicate) {
		// TODO: implement
		throw new RuntimeException("anyMatch is not implemented yet");
	}

	@Override
	public boolean allMatch(Predicate<? super ElementType> predicate) {
		// TODO: implement
		throw new RuntimeException("allMatch is not implemented yet");
	}

	@Override
	public boolean noneMatch(Predicate<? super ElementType> predicate) {
		SingleElementConsumer<ElementType> consumer = new SingleElementConsumer<>();
		return evaluate(() -> {
			while (tryAdvance(consumer)) {
				if (predicate.test(consumer.element)) {
					return false;
				}
			}
			return true;
		});
	}

	@Override
	public Optional<ElementType> findFirst() {
		SingleElementConsumer<ElementType> singleElementConsumer = new SingleElementConsumer<>();
		return evaluate(() -> tryAdvance(singleElementConsumer)
			? Optional.of(singleElementConsumer.element)
			: Optional.empty()
		);
	}

	public ObjectSqlStreamChain<ElementType> setFetchSize(int fetchSize) {
		getRootSqlDataSource().setFetchSize(fetchSize);
		return this;
	}

	@Override
	public Optional<ElementType> findAny() {
		return findFirst();
	}

	@Override
	public Iterator<ElementType> iterator() {
		// TODO: implement
		throw new RuntimeException("stream iterator is not implemented yet");
	}

	@Override
	public Spliterator<ElementType> spliterator() {
		// TODO: implement
		throw new RuntimeException("stream spliterator is not implemented yet");
	}

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public Stream<ElementType> sequential() {
		return this;
	}

	@Override
	public Stream<ElementType> parallel() {
		throw new RuntimeException("parallel is not implemented yet");	// TODO: implement
	}

	@Override
	public Stream<ElementType> unordered() {
		return this;
	}

	@Override
	public Stream<ElementType> onClose(Runnable closeHandler) {
		throw new RuntimeException("onClose is not implemented yet");	// TODO: implement
	}

	@Override
	public void close() {
		source.close();
	}
}

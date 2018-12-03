package io.resql.orm.stream;

import io.resql.PostgresqlDbPipe;
import io.resql.orm.AccessorFactory;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SqlStream<T> extends SqlStreamPipeline<T> {
	private Supplier<T> factory;
	private AccessorFactory accessorFactory;
	private CharSequence sql;
	private Object[] params;
	private Logger log;
	private DataSource dataSource;

	public SqlStream(DataSource dataSource, AccessorFactory accessorFactory, Logger log, CharSequence sql, Object[] params, Supplier<T> factory) {
		this.log = log;
		this.factory = factory;
		this.sql = sql;
		this.params = params;
		this.dataSource = dataSource;
		this.accessorFactory = accessorFactory;
	}

	@Override
	public Stream<T> filter(Predicate<? super T> predicate) {
		// TODO: implement
		return null;
	}

	@Override
	public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super T> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public LongStream mapToLong(ToLongFunction<? super T> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> distinct() {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> sorted() {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> sorted(Comparator<? super T> comparator) {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> peek(Consumer<? super T> action) {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> limit(long maxSize) {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> skip(long n) {
		// TODO: implement
		return null;
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		try (
			var connection = dataSource.getConnection();
			var resultSet = query(connection);
		) {
			var accessor = accessorFactory.createOrGet(sql, PostgresqlDbPipe.getColumnTypes(resultSet.getMetaData()), factory);
			while (resultSet.next()) {
				T data = accessor.get(resultSet);
				action.accept(data);
			}
		} catch (SQLException sqle) {
			log.error("Error executing query\n" + toPlainRequest(sql, params), sqle);
		}
	}

	private ResultSet query(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql.toString());
		PostgresqlDbPipe.fillStatementWithParams(statement, params);
		return statement.executeQuery();
	}

	private String toPlainRequest(CharSequence sql, Object[] params) {
		// TODO: implement
		return sql + " (" + params + ")";
	}

	@Override
	public void forEachOrdered(Consumer<? super T> action) {
		// TODO: implement
	}

	@Override
	public Object[] toArray() {
		// TODO: implement
		return new Object[0];
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		// TODO: implement
		return null;
	}

	@Override
	public T reduce(T identity, BinaryOperator<T> accumulator) {
		// TODO: implement
		return null;
	}

	@Override
	public Optional<T> reduce(BinaryOperator<T> accumulator) {
		// TODO: implement
		return Optional.empty();
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		// TODO: implement
		return null;
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
		// TODO: implement
		return null;
	}

	@Override
	public <R, A> R collect(Collector<? super T, A, R> collector) {
		// TODO: implement
		return null;
	}

	@Override
	public Optional<T> min(Comparator<? super T> comparator) {
		// TODO: implement
		return Optional.empty();
	}

	@Override
	public Optional<T> max(Comparator<? super T> comparator) {
		// TODO: implement
		return Optional.empty();
	}

	@Override
	public long count() {
		// TODO: implement
		return 0;
	}

	@Override
	public boolean anyMatch(Predicate<? super T> predicate) {
		// TODO: implement
		return false;
	}

	@Override
	public boolean allMatch(Predicate<? super T> predicate) {
		// TODO: implement
		return false;
	}

	@Override
	public boolean noneMatch(Predicate<? super T> predicate) {
		// TODO: implement
		return false;
	}

	@Override
	public Optional<T> findFirst() {
		// TODO: implement
		return Optional.empty();
	}

	@Override
	public Optional<T> findAny() {
		// TODO: implement
		return Optional.empty();
	}

	@Override
	public Iterator<T> iterator() {
		// TODO: implement
		return null;
	}

	@Override
	public Spliterator<T> spliterator() {
		// TODO: implement
		return null;
	}

	@Override
	public boolean isParallel() {
		// TODO: implement
		return false;
	}

	@Override
	public Stream<T> sequential() {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> parallel() {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> unordered() {
		// TODO: implement
		return null;
	}

	@Override
	public Stream<T> onClose(Runnable closeHandler) {
		// TODO: implement
		return null;
	}

	@Override
	public void close() {

	}
}

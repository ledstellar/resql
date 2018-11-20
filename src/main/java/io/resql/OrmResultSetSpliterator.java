package io.resql;

import io.resql.orm.Accessor;

import java.sql.ResultSet;
import java.util.Spliterator;
import java.util.function.Consumer;

public class OrmResultSetSpliterator<OrmType> implements Spliterator<OrmType> {
	private Accessor<OrmType> accessor;
	private ResultSet resultSet;

	OrmResultSetSpliterator(ResultSet resultSet, Accessor<OrmType> accessor) {
		this.resultSet = resultSet;
		this.accessor = accessor;
	}

	@Override
	public boolean tryAdvance(Consumer<? super OrmType> action) {
		OrmType pojo = accessor.get(resultSet);
		if (pojo == null) {
			return false;
		}
		action.accept(pojo);
		return true;
	}

	@Override
	public Spliterator<OrmType> trySplit() {
		return null;    // cannot be split
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	@Override
	public int characteristics() {
		return NONNULL;
	}
}

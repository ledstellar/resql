package ru.resql.orm.stream;

import ru.resql.SqlException;

import java.util.function.Consumer;

class LimitChain<ElementType> extends ObjectSqlStreamChain<ElementType> {
	private long remain;

	LimitChain(ObjectSqlStreamChain<ElementType> source, long limit) {
		super(source);
		remain = limit;
	}

	@Override
	boolean tryAdvance(Consumer<? super ElementType> action) throws SqlException {
		if (remain > 0) {
			--remain;
			return source.tryAdvance(action);
		}
		return false;
	}
}

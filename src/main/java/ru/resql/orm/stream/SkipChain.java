package ru.resql.orm.stream;

import ru.resql.SqlException;

import java.util.function.Consumer;

class SkipChain<ElementType> extends ObjectSqlStreamChain<ElementType> {
	private long toBeSkipped;

	SkipChain(ObjectSqlStreamChain<ElementType> source, long limit) {
		super(source);
		toBeSkipped = limit;
	}

	@Override
	boolean tryAdvance(Consumer<? super ElementType> action) throws SqlException {
		while (toBeSkipped > 0) {
			if (!source.tryAdvance(elementType -> { /* just skip */ })) {
				return false;
			}
			--toBeSkipped;
		}
		return source.tryAdvance(action);
	}
}

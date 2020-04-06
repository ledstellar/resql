package ru.resql.orm.stream;

import ru.resql.SqlException;

import java.util.function.*;

class FilterChain<ElementType> extends ObjectSqlStreamChain<ElementType> {
	private final Predicate<? super ElementType> predicate;

	FilterChain(ObjectSqlStreamChain<ElementType> source, Predicate<? super ElementType> predicate) {
		super(source);
		this.predicate = predicate;
	}

	@Override
	boolean tryAdvance(Consumer<? super ElementType> action) throws SqlException {
		return source.tryAdvance(el -> {
			if (predicate.test(el)) {
				action.accept(el);
			}
		});
	}
}

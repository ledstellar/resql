package ru.resql.orm.stream;

import ru.resql.SqlException;

import java.util.function.Consumer;

class PeekChain<ElementType> extends ObjectSqlStreamChain<ElementType> {
	private final Consumer<? super ElementType> action;

	PeekChain(ObjectSqlStreamChain<ElementType> source, Consumer<? super ElementType> action) {
		super(source);
		this.action = action;
	}

	@Override
	boolean tryAdvance(Consumer<? super ElementType> action) throws SqlException {
		return source.tryAdvance(el -> {
			action.accept(el);
			this.action.accept(el);
		});
	}
}

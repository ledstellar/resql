package ru.resql.orm.stream;

import java.util.function.Consumer;

/**
 * Consumer that simply accept and store one element
 * @param <ElementType> type of consuming element
 */
class SingleElementConsumer<ElementType> implements Consumer<ElementType> {
	ElementType element;

	@Override
	public void accept(ElementType element) {
		this.element = element;
	}
}

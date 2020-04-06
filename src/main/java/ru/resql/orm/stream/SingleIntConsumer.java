package ru.resql.orm.stream;

import java.util.function.IntConsumer;

public class SingleIntConsumer implements IntConsumer {
	public int element;

	@Override
	public void accept(int element) {
		this.element = element;
	}
}

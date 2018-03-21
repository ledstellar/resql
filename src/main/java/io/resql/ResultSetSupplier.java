package io.resql;

import java.util.Iterator;

public class ResultSetSupplier<T> implements Iterator<T> {
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		return null;
	}
}

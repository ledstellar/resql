package io.resql;

import io.resql.orm.Accessor;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.function.Supplier;

public class ResultSetSupplier<T> implements Iterator<T> {

	ResultSetSupplier(ResultSet resultSet, Accessor accessor) {
		// todo: implement
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		return null;
	}
}

package io.resql;

public interface Batch<OrmType> {
	void add(OrmType value);
}

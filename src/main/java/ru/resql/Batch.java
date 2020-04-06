package ru.resql;

public interface Batch<OrmType> {
	void add(OrmType value);
}

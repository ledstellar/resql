package io.resql;

@FunctionalInterface
public interface QueryBuilder<OrmType> {
	CharSequence build(Class<OrmType> type, DbPipe pipe);
}

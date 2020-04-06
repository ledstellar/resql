package ru.resql;

import ru.resql.orm.stream.*;

public interface SqlDataSourceKeeper<ElementType> {
	SqlDataSource getSqlDataSource();
	ObjectSqlStreamChain<ElementType> setFetchSize(int fetchSize);
}

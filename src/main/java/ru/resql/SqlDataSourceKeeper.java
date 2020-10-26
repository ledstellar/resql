package ru.resql;

import ru.resql.orm.stream.*;

public interface SqlDataSourceKeeper<ElementType> {
	SelectWithParamSqlDataSource getSqlDataSource();
	ObjectSqlStreamChain<ElementType> setFetchSize(int fetchSize);
}

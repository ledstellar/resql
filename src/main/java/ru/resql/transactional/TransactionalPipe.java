package ru.resql.transactional;

import ru.resql.orm.stream.*;

import java.util.Collection;
import java.util.function.Supplier;

public interface TransactionalPipe {
	/**
	 * Execute prepared sql query with optional params and return result as a stream POJO objects.
	 * ORM is used to transfer data from SQL resultset records to stream. Convertes can be used to convert resultset
	 * columns to object field types.
	 * @param factory object factory. ReturnType::new is the simplest case.
	 * @param sql query text with an optional sign <code>?</code> as an ordered argument placeholder
	 * @param args query arguments if any
	 * @param <ReturnType> type of objects that factory supplies
	 * @return stream of objects initialized with resultset records
	 */
	<ReturnType> OrmSqlStreamRecordSource<ReturnType> select(Supplier<ReturnType> factory, CharSequence sql, Object... args);

	/**
	 * Execute prepared sql query with optional params and return result as a stream of objects. Non ORM method version.
	 * Assignment is used to transfer data from SQL resultset records to stream. Converters can be used to convert resultset
	 * data type to stream object's class type.
	 * @param clazz stream object class
	 * @param sql query text with an optional sign <code>?</code> as an ordered argument placeholder.
	 * SQL query should return resultset with the only column.
	 * @param args query arguments if any
	 * @param <ReturnType> type of objects that factory supplies
	 * @return stream of objects of first resultset column
	 */
	<ReturnType> ObjectSqlStreamRecordSource<ReturnType> select(Class<ReturnType> clazz, CharSequence sql, Object... args);

	IntSqlStreamRecordSource selectIntegers(CharSequence sql, Object... args);

	/*
	DoubleSqlStreamRecordSource selectDouble(CharSequence sql, Object ... args);
	LongSqlStreamRecordSource selectLong(CharSequence sql, Object ... args);
	*/

	/**
	 * Execute given (supposedly update) query
	 * @param sql the query
	 * @param params query params
	 * @return number of records processed (if database returns this info)
	 */
	int execute(CharSequence sql, Object... params);

	void batchSync(Collection<?> data);
}

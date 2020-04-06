package ru.resql;

import ru.resql.transactional.*;

/**
 * Database connection(s) tied to particular class.
 */
public interface DbPipe extends TransactionalPipe {
	void transactional(Transactional transactional) throws Throwable;
}

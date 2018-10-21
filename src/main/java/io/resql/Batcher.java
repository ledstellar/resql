package io.resql;

/**
 * Application logic implementation that can process batch queries with ORM types
 * @param <OrmType> ORM type that maps to batch query parameters
 */
@FunctionalInterface
public interface Batcher<OrmType> {
	/**
	 * Application logic implementation with ability to use database batch queries
	 * @param batch this should be used to pass parameters to a batch query
	 * @throws Throwable when this happens the query rollback
	 */
	void run(Batch<OrmType> batch) throws Throwable;
}

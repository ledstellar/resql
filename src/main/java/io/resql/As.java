package io.resql;

public class As {
	public static <T> T single(ResultSetSupplier<T> resultSet) {
		if (resultSet.hasNext()) {
			return resultSet.next();
		} else {
			return null;
		}
	}

	public static <T> T singleOnly(ResultSetSupplier<T> resultSet) {
		if (!resultSet.hasNext()) {
			throw new ConstraintException("Single record expected but got no records");
		}
		T obj = resultSet.next();
		if (resultSet.hasNext()) {
			throw new ConstraintException("Single record expected but got more");
		}
		return obj;
	}
}

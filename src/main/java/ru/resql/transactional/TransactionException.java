package ru.resql.transactional;

public class TransactionException extends RuntimeException {
	public TransactionException(Throwable cause) {
		super(cause);
	}
}

package io.resql;

public abstract class Transaction {
	DbPipe transactionalPipe;

	public Transaction( DbPipe dbPipe ) {
//		transactionalPipe = dbPipe.getDbManager().getTransactionalPipe( dbPipe.getLogger() );
	}

	abstract protected void run( DbPipe pipe );
}

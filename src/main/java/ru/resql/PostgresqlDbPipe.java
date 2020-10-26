package ru.resql;

import org.slf4j.Logger;
import ru.resql.transactional.*;

public class PostgresqlDbPipe extends DbPipeBase implements DbPipe {
	PostgresqlDbPipe(DbManager dbManager, Logger log) {
		super(dbManager, log);
	}

	@Override
	protected ConnectionWrapper getConnectionWrapper(boolean isReadOnly) {
		return new ConnectionWrapper(dbManager, isReadOnly);
	}

/*	@Override
	public <ReturnType> ReturnType batch(Mapper<ReturnType> mapper, CharSequence sql, Object... args) {
		return null;
	} */

/*	@Override
	public <ReturnType> SqlStreamRecordSource<ReturnType> streamBatch(Supplier<ReturnType> factory, StreamMapper<ReturnType> mapper, CharSequence sql, Object... args) {
		// TODO: implement
		return null;
	} */

	@Override
	public void transactional(Transactional transactional) throws TransactionException {
		TransactionalConnectionWrapper connectionWrapper = new TransactionalConnectionWrapper(dbManager);
		TransactionalPipeImpl transactionalPipeImpl = new TransactionalPipeImpl(dbManager, log, connectionWrapper);
		try {
			transactional.run(transactionalPipeImpl);
			connectionWrapper.commit();
		} catch (Throwable th) {
			connectionWrapper.rollback();
			throw new TransactionException(th);
		} finally {
			connectionWrapper.close();
		}
	}
}

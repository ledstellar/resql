package ru.resql.transactional;

import org.slf4j.Logger;
import ru.resql.*;

public class TransactionalPipeImpl extends DbPipeBase {
	TransactionalConnectionWrapper connectionWrapper;

	public TransactionalPipeImpl(DbManager dbManager, Logger log, TransactionalConnectionWrapper connectionWrapper) {
		super(dbManager, log);
		this.connectionWrapper = connectionWrapper;
	}

	@Override
	protected ConnectionWrapper getConnectionWrapper(boolean isReadOnly) {
		return connectionWrapper;
	}

/*	@Override
	public <ReturnType> ReturnType batch(Mapper<ReturnType> mapper, CharSequence sql, Object... args) {
		// TODO: implement
		throw new RuntimeException("Not implemented yet");
	} */
}

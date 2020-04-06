package ru.resql.transactional;

import lombok.extern.slf4j.Slf4j;
import ru.resql.*;

import java.sql.SQLException;

@Slf4j
public class TransactionalConnectionWrapper extends ConnectionWrapper {
	boolean isStatementRunning;

	public TransactionalConnectionWrapper(DbManager dbManager) {
		super(dbManager, true); // read only == transactional == ! autocommit
	}

	@Override public void close() {
		isStatementRunning = false;
	}

	public void commit() throws SqlException {
		if (!isInitiated) {
			return;
		}
		try {
			connection.commit();
		} catch (SQLException sqlException) {
			throw new SqlException("Exception in commit", sqlException);
		} finally {
			super.close();
		}
	}

	public void rollback() {
		if (!isInitiated) {
			return;
		}
		try {
			connection.rollback();
		} catch (SQLException sqlException) {
			log.error("Exception in rollback", sqlException);
		} finally {
			super.close();
		}
	}

	public boolean initiateIfNeed() {
		if (super.initiateIfNeed()) {
			isStatementRunning = true;
			return true;
		}
		if (isStatementRunning) {
			return false;
		}
		isStatementRunning = true;
		return true;
	}
}

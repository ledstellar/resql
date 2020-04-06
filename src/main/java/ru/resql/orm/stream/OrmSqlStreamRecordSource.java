package ru.resql.orm.stream;

import ru.resql.*;
import ru.resql.orm.*;
import ru.resql.orm.converters.ConverterFrames;
import ru.resql.util.ResultSetDebugFormatter;

import java.sql.SQLException;
import java.util.function.*;

public class OrmSqlStreamRecordSource<ElementType> extends ObjectSqlStreamChain<ElementType>
	implements SqlDataSourceKeeper<ElementType>
{
	private final Supplier<ElementType> factory;
	private ElementType firstElement;
	private final ConverterFrames converterFrames;
	private final AccessorFactory accessorFactory;
	private Accessor<ElementType> accessor;
	private final SqlDataSource sqlDataSource;

	public OrmSqlStreamRecordSource(
		ConverterFrames converterFrames, SqlDataSource sqlDataSource,
		AccessorFactory accessorFactory, Supplier<ElementType> factory
	) {
		super(null);
		this.converterFrames = converterFrames;
		this.sqlDataSource = sqlDataSource;
		this.accessorFactory = accessorFactory;
		this.factory = factory;
	}

	@Override
	public SqlDataSource getSqlDataSource() {
		return sqlDataSource;
	}

	protected void setupResultSetMapper() throws SQLException {
		if (!sqlDataSource.resultSet.next()) {
			// If result set is empty then do not get any element from factory. We don't need this element actually
			return;
		}
		// we will use this element further as the first stream result
		firstElement = factory.get();
		accessor = accessorFactory.createOrGet(sqlDataSource, firstElement.getClass(), converterFrames);
	}

	@Override boolean tryAdvance(Consumer<? super ElementType> action) throws SqlException {
		try {
			if (sqlDataSource.runQueryIfNotYet()) {
				setupResultSetMapper();
			}
			if (firstElement != null || sqlDataSource.resultSet.next()) {
				final ElementType element;
				if (firstElement == null) {
					element = factory.get();
				} else {
					element = firstElement;
					firstElement = null;
				}
				accessor.init(element, sqlDataSource.resultSet);
				action.accept(element);
				return true;
			} else {
				return false;
			}
		} catch (SQLException sqlException) {
			SqlException sqlE = new SqlException("Error receiving next record", sqlException);
			sqlE.setRequestSql(sqlDataSource.getQueryDebug());
			if (sqlDataSource.resultSet != null) {
				sqlE.setCurrentResultSet(ResultSetDebugFormatter.format(sqlDataSource.resultSet));
			}
			throw sqlE;
		}
	}

	@Override
	public void close() {
		sqlDataSource.close();
	}
}

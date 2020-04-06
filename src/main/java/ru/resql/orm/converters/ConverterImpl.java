package ru.resql.orm.converters;

public abstract class ConverterImpl<ResultType> implements Converter<ResultType> {
	protected final String columnDescription;
	protected final String fieldDescription;

	protected ConverterImpl(String columnDescription, String fieldDescription) {
		this.columnDescription = columnDescription;
		this.fieldDescription = fieldDescription;
	}

	@Override
	public String toString() {
		return columnDescription + " --(" + getClass().getSimpleName() + ")--> " + fieldDescription;
	}
}

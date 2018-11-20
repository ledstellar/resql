package io.resql.orm.converters;

import java.lang.reflect.Field;

abstract class FromDbConverterBase implements FromDbConverter {
	protected int columnIndex;
	protected Field field;

	FromDbConverterBase(int columnIndex, Field toField) {
		this.columnIndex = columnIndex;
		this.field = toField;
	}
}

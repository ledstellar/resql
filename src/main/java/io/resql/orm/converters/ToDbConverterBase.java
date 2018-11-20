package io.resql.orm.converters;

import java.lang.reflect.Field;

abstract class ToDbConverterBase implements ToDbTypeConverter {
	protected int paramIndex;
	protected Field field;

	ToDbConverterBase(int columnIndex, Field field) {
		this.paramIndex = columnIndex;
		this.field = field;
	}
}

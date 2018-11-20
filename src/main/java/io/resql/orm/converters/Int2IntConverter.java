package io.resql.orm.converters;

@SuppressWarnings("unused")
public class Int2IntConverter extends Int4IntConverter {
	@Override
	public String getDbColumnType() {
		return "int2";
	}
}

package io.resql.orm.converters;

import java.lang.reflect.Field;
import java.sql.*;

public class IntegerConverter {
	String getDdbColumnType() {
		return "integer";
	}


}

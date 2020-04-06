package ru.resql.orm;

import org.junit.jupiter.api.*;

public class SchemaDroppingMethodToTheEnd implements MethodOrderer {
	public static final String DROP_SCHEMA_METHOD_NAME = "atLastDropTestSchema";

	@Override
	public void orderMethods(MethodOrdererContext context) {
		context.getMethodDescriptors().sort(
			(MethodDescriptor m1, MethodDescriptor m2)-> {
				String name1 = m1.getMethod().getName();
				String name2 = m2.getMethod().getName();
				if (DROP_SCHEMA_METHOD_NAME.equals(name1)) {
					if (DROP_SCHEMA_METHOD_NAME.equals(name2)) {
						return 0;
					}
					return 1;
				}
				if (DROP_SCHEMA_METHOD_NAME.equals(name2)) {
					return -1;
				}
				return name1.compareToIgnoreCase(name2);
			}
		);
	}
}
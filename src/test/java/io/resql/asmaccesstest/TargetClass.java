package io.resql.asmaccesstest;

import static org.junit.Assert.assertEquals;

public class TargetClass {
	/*private */ int privateAccessInt;
	String defaultAccessString;
	public double publicAccessDouble;
	protected TargetClass protectedMySelf;

	void checkValuesSet() {
		assertEquals( 1, privateAccessInt );
		assertEquals( 2., publicAccessDouble, 0. );
		assertEquals( "строка", defaultAccessString );
	}
}

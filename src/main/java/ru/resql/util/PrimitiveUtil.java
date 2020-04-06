package ru.resql.util;

import java.util.*;

public class PrimitiveUtil {
	private static final Class<?>[][] objectToPrimitiveSrc = {
		{Integer.class, int.class},
		{Long.class, long.class},
		{Boolean.class, boolean.class},
		{Double.class, double.class},
		{Float.class, float.class}
	};

	public static Map<Class<?>, Class<?>> objectToPrimitives;
	public static Map<Class<?>, Class<?>> primitiveToObjects;

	static {
		HashMap<Class<?>, Class<?>> otp = new HashMap<>();
		HashMap<Class<?>, Class<?>> pto = new HashMap<>();
		for (Class<?>[] pair : objectToPrimitiveSrc) {
			Class<?> objectClass = pair[0];
			Class<?> primitiveClass = pair[1];
			otp.put(objectClass, primitiveClass);
			pto.put(primitiveClass, objectClass);
		}
		objectToPrimitives = Collections.unmodifiableMap(otp);
		primitiveToObjects = Collections.unmodifiableMap(pto);
	}
}

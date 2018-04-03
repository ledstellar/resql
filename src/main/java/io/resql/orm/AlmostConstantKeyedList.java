package io.resql.orm;

import java.util.ArrayList;

/**
 * Storage class for small amount of almost constant objects. That is, references to String literals fot example.
 * These object can be effectively compared by references.
 * Unsynchronized implementation!
 * @param <T> type of keys
 */
class AlmostConstantKeyedList<T> {
	private final ArrayList<T> values = new ArrayList<>();

	/**
	 * Scan for given key and return its index in list. If ke is not found then reference to this key added.
	 * Caller can later check reference equality of key with returned index to find out either this key
	 * was found by reference equality or by value equality.
	 * @param key the key to be found or added
	 * @return index of given key in this list
	 */
	public int indexOf(T key) {
		int index = 0;
		// first scan by reference equality
		for ( T value : values ) {
			if ( value == key ) {
				return index;
			}
			++ index;
		}
		// not found. Let's retry by values
		index = 0;
		for ( T value : values ) {
			if ( value.equals( key ) ) {
				return index;
			}
			++ index;
		}
		values.add( key );
		return values.size() - 1;
	}

	public T get( int index ) {
		return values.get( index );
	}
}

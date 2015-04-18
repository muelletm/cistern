package marmot.util;

import java.util.Arrays;

public class HashableIntArray {

	private int[] array_;
	
	public HashableIntArray(int[] array) {
		array_ = array.clone();
	}

	public int[] getArray() {
		return array_;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(array_);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (other.getClass() != getClass())
			return false;
		
		HashableIntArray array = (HashableIntArray) other;
		return Arrays.equals(array_, array.array_);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(array_);
	}
	
}

package marmot.util;

public class MutableInteger {

	int value_;
	
	public MutableInteger() {
		value_ = 0;
	}
	
	public void add(int i) {
		value_ += 1;
	}
	
	public int get() {
		return value_;
	}

}

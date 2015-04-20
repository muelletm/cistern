// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

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

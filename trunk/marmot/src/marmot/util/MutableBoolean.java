// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

public class MutableBoolean {

	private boolean value_;
	
	public MutableBoolean(boolean value) {
		value_ = value;
	}
	
	public void set(boolean value) {
		value_ = value;
	}
	
	public boolean get() {
		return value_;
	}
	
}

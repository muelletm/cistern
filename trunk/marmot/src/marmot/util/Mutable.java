// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

public class Mutable<T> {

	private T value_;

	public Mutable(T value) {
		value_ = value;
	}

	public void set(T value) {
		value_ = value;
	}

	public T get() {
		return value_;
	}

	@Override
	public String toString() {
		return (value_ == null) ? "null" : value_.toString();
	}

}

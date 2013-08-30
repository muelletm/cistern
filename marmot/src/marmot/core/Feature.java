// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.io.Serializable;
import java.util.Arrays;

public class Feature implements Serializable {
	private static final long serialVersionUID = 1L;
	private int[] bytes_;
	private short current_length_;
	private short current_bit_index_;

	public Feature(int[] bytes, short current_length, short current_bit_index) {
		bytes_ = bytes;
		current_length_ = current_length;
		current_bit_index_ = current_bit_index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes_);
		result = prime * result + current_bit_index_;
		result = prime * result + current_length_;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (!Arrays.equals(bytes_, other.bytes_))
			return false;
		if (current_bit_index_ != other.current_bit_index_)
			return false;
		if (current_length_ != other.current_length_)
			return false;
		return true;
	}

	public int[] getBytes() {
		return bytes_;
	}

	public short getCurrentBitIndex() {
		return current_bit_index_;
	}

	public short getCurrentLength() {
		return current_length_;
	}
	
}

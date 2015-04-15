// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.Serializable;
import java.util.Arrays;

import marmot.core.Feature;



public class Encoder implements Serializable {
	
	private int[] bytes_;
	private short current_array_length_;
	private short current_bit_index_;
	private short stored_array_length_;
	private short stored_bit_index_;
	private int stored_byte_;

	public Encoder(int capacity) {
		bytes_ = new int[capacity];
		reset();
	}

	public static int bitsNeeded(int max_value) {
		int num_bits = 1;
		while (true) {
			max_value /= 2;		
			if (max_value == 0) {
				break;
			}
			num_bits += 1;
		}		
		return num_bits;
	}

	public void append(boolean value) {
		append(value ? 0 : 1, 1);
	}

	public void append(int value, int bits_needed) {
		assert value >= 0;
		assert bitsNeeded(value) <= bits_needed : value;

		while (bits_needed != 0) {
			if (current_bit_index_ == Integer.SIZE) {
				current_array_length_ ++;
				current_bit_index_ = 0;
			}

			int bits_left = Integer.SIZE - current_bit_index_;
			int bits = Math.min(bits_left, bits_needed);
			int mask = (2 << (bits - 1)) - 1;			
			int b = value & mask;
			value >>= bits;
			bits_needed -= bits;
			
			bytes_[current_array_length_ - 1] += b << current_bit_index_;				
			current_bit_index_ += bits;
		}
		assert value == 0;
	}

	public Feature getFeature() {
		int[] bytes = new int[bytes_.length];
		System.arraycopy(bytes_, 0, bytes, 0, current_array_length_);
		return new Feature(bytes, current_array_length_, current_bit_index_);
	}

	public void reset() {
		current_array_length_ = 0;
		current_bit_index_ = Integer.SIZE;
		storeState();
		Arrays.fill(bytes_, 0);
	}

	public Feature getFeature(boolean flag) {
		return getFeature();
	}

	public void storeState() {
		stored_array_length_ = current_array_length_;
		stored_bit_index_ = current_bit_index_;
		if (current_array_length_ > 0)
			stored_byte_ = bytes_[current_array_length_  - 1];
		else
			stored_byte_ = 0;
	}

	public void restoreState() {
		Arrays.fill(bytes_, stored_array_length_ , current_array_length_, 0);
		current_array_length_ = stored_array_length_;
		current_bit_index_ = stored_bit_index_;
		if (current_array_length_ > 0)
			bytes_[current_array_length_ - 1] = stored_byte_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes_);
		result = prime * result + current_array_length_;
		result = prime * result + current_bit_index_;
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
		Encoder other = (Encoder) obj;
		if (!Arrays.equals(bytes_, other.bytes_))
			return false;
		if (current_array_length_ != other.current_array_length_)
			return false;
		if (current_bit_index_ != other.current_bit_index_)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Encoder [bytes_=" + Arrays.toString(bytes_)
				+ ", current_array_length_=" + current_array_length_
				+ ", current_bit_index_=" + current_bit_index_
				+ ", stored_array_length_=" + stored_array_length_
				+ ", stored_bit_index_=" + stored_bit_index_
				+ ", stored_byte_=" + stored_byte_ + "]";
	}

	
	
}

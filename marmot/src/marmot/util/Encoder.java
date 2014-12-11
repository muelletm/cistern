// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.Arrays;

import marmot.core.Feature;



public class Encoder {
	private int[] bytes_;
	private short current_array_length_;
	private short current_bit_index_;

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
		assert bitsNeeded(value) <= bits_needed;

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
		Arrays.fill(bytes_, 0);
	}

	public Feature getFeature(boolean flag) {
		return getFeature();
	}

}

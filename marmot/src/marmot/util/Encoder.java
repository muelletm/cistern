// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.util.Arrays;

import marmot.core.Feature;



public class Encoder {
	private int[] bytes_;
	private short current_array_length_;
	private short current_bit_index_;

	private static final double log2_ = Math.log(2);
	
	public Encoder(int capacity) {
		bytes_ = new int[capacity];
		reset();
	}

	public static int bitsNeeded(int max_value) {
		int num_bits = (int) Math.ceil((Math.log(max_value + 1) / log2_));
		// rangeCheck(current_array_length_ + num_bits - ((8 * Integer.SIZE) - current_bit_index_));
		return num_bits;
	}

	public void append(boolean value) {
		append(value ? 0 : 1, 1);
	}

	public void append(int value, int bits_needed) {
		//System.err.println(value + " " + bits_needed);
		
		assert value >= 0;
		assert bitsNeeded(value) <= bits_needed;
		
		//assert value <= max_value;
		//int bits_needed = bitsNeeded(max_value);
		
		//System.err.println("bits nedded: " + bits_needed);
		
		while (bits_needed != 0) {
			if (current_bit_index_ == Integer.SIZE) {
				current_array_length_ ++;
				current_bit_index_ = 0;
			}

			int bits_left = Integer.SIZE - current_bit_index_;
			//System.err.println("bits left: " + bits_left);
			
			int bits = Math.min(bits_left, bits_needed);
			//System.err.println("bits: " + bits);
			

			//System.err.println("max value: " + Integer.toBinaryString(max_value));
			//System.err.println("value: " + Integer.toBinaryString(value));

			int mask = (2 << (bits - 1)) - 1;
			
			//System.err.println("mask: " + Integer.toBinaryString(mask));
			
			int b = value & mask;
			value >>= bits;
			bits_needed -= bits;
			
			//System.err.println("b: " + Integer.toBinaryString(b));
			bytes_[current_array_length_ - 1] += b << current_bit_index_;	
			
			//System.err.println("byte: " + Integer.toBinaryString(bytes_[current_array_length_ - 1]));
			current_bit_index_ += bits;
		}
		
		
		//System.err.println(Arrays.toString(bytes_));
		
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

	public static void main(String[] args) {
		Encoder encoder = new Encoder(5);
		encoder.append(3, 5);
		encoder.append(7, 12);
		encoder.append(1, 1);
		encoder.append(241342312, 250000000);
	}

	public Feature getFeature(boolean flag) {
		return getFeature();
	}

}

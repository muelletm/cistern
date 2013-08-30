// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import marmot.core.Feature;

public class Decoder {

	private int[] bytes_;
	private short byte_index_;
	private short bit_index_;
	private short total_byte_length;
	private short total_bit_index;
	
	public Decoder(Feature feature) {
		bytes_ = feature.getBytes();
		total_bit_index = feature.getCurrentBitIndex();
		total_byte_length = (short)(feature.getCurrentLength() - 1);
	}
	
	int decode(int bits) {	
		if (byte_index_ > total_byte_length || (bit_index_ > total_bit_index && byte_index_ == total_byte_length))
			return -1;
		
		int bits_read = 0;
		int value = 0;

		
		while (bits_read < bits) {
			int read_now = Math.min(Integer.SIZE - bit_index_, bits - bits_read);
			assert read_now > 0;
			
			if (byte_index_ > total_byte_length) {
				return -1;
			}
			int byte_value = bytes_[byte_index_];
			byte_value >>= bit_index_;
			int mask = (2 << (read_now - 1)) - 1;			
			int new_value = byte_value & mask;
			
			//System.err.format("new_value %s\n", Integer.toBinaryString(new_value));
			
			
			new_value <<= bits_read;
			
			//System.err.format("new_value shifted %s %d\n", Integer.toBinaryString(new_value), new_value);
			//System.err.format("value %s %d\n", Integer.toBinaryString(value), value);
			value += new_value;
			
			//System.err.format("value %s %d\n", Integer.toBinaryString(value), value);
			
			bits_read += read_now;			
			bit_index_ += read_now;
			if (bit_index_ > total_bit_index && byte_index_ == total_byte_length) {
				return -1;
			}
			if (bit_index_ == Integer.SIZE) {
				bit_index_ = 0;
				byte_index_ ++;
			}
		}
		return value;
	}
	
	public static void main(String[] args) {
		Encoder encoder = new Encoder(3);
		
		encoder.append(0, 1);
		encoder.append(0, 2);
		encoder.append(8, 5);
		encoder.append(65, 7);
		encoder.append(20, 7);
		encoder.append(14, 7);
		encoder.append(6, 7);
		encoder.append(5, 7);
		encoder.append(3, 7);
		encoder.append(16, 7);
		encoder.append(6, 7);

		
		Feature f = encoder.getFeature();
		
		
		
		Decoder decoder = new Decoder(f);
		
		assert 0 == decoder.decode(1);
		assert 0 == decoder.decode(2);
		assert 8 == decoder.decode(5);
		assert 65 == decoder.decode(7);
		assert 20 == decoder.decode(7);
		assert 14 == decoder.decode(7);
		assert 6 == decoder.decode(7);
		assert 5 == decoder.decode(7);
		assert 3 == decoder.decode(7);
		assert 16 == decoder.decode(7);
		assert 6 == decoder.decode(7);
		assert -1 == decoder.decode(1);
		
	}

	public short getByteLength() {
		return byte_index_;
	}

	public short getBitIndex() {
		return bit_index_;
	}
	
	public short getTotalByteLength() {
		return total_byte_length;
	}

	public short getTotalBitIndex() {
		return total_bit_index;
	}
	
}

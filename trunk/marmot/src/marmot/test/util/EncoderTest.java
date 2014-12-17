// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.util;

import static org.junit.Assert.*;
import marmot.util.Encoder;

import org.junit.Test;

public class EncoderTest {

	@Test
	public void testBitsNeeded() {
		assertEquals(1, Encoder.bitsNeeded(0));
		assertEquals(1, Encoder.bitsNeeded(1));
		assertEquals(2, Encoder.bitsNeeded(2));
		assertEquals(2, Encoder.bitsNeeded(3));
		assertEquals(3, Encoder.bitsNeeded(4));
		assertEquals(3, Encoder.bitsNeeded(5));
		assertEquals(3, Encoder.bitsNeeded(6));
		assertEquals(3, Encoder.bitsNeeded(7));
		assertEquals(4, Encoder.bitsNeeded(8));
		
		for (int i = 0; i < 1000; i++) {
			int expected_num_bits = Integer.toBinaryString(i).length();
			assertEquals(expected_num_bits, Encoder.bitsNeeded(i));
		}
	}
}

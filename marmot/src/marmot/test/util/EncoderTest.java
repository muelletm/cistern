// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import marmot.util.Copy;
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
	
	@Test
	public void testStoreState() {
	
		Random random = new Random(42);
		
		for (int trial = 0; trial < 10; trial ++) {
			Encoder encoder = new Encoder(6);
			randomAppend(random, encoder, 100, 15);
			
			
			Encoder copy = Copy.clone(encoder);
			
			encoder.storeState();
			for (int append_trial = 0; append_trial < 10; append_trial++) {
				randomAppend(random, encoder, 100, 15);
				encoder.restoreState();
				System.err.println(copy);
				System.err.println(encoder);
				assertEquals(copy, encoder);
			}
			
		}
		
		
	}

	private void randomAppend(Random random, Encoder encoder, int max_max_value, int max_num_append) {
		int num_appends = random.nextInt(max_num_append + 1);
		for (int i=0; i<num_appends; i++) {
			int max_value = random.nextInt(max_max_value + 1);
			int value = random.nextInt(max_value + 1);
			encoder.append(value, Encoder.bitsNeeded(max_value));
		}		
	}
}

// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import marmot.tokenize.Pair;

import org.junit.Test;

public class PairTest {

	public List<String> input() {
		String sentence = "I'm not so sure that this will work, but let's hope it does. ";
		List<String> iob = Arrays.asList("T", "T", "I", "O", "T", "I", "I", "O", 
				"T", "I", "O", "T", "I", "I", "I", "O", "T", "I", "I", "I", "O", 
				"T", "I", "I", "I", "O", "T", "I", "I", "I", "O", "T", "I", "I", 
				"I", "T", "O", "T", "I", "I", "O", "T", "I", "I", "T", "I", "O", 
				"T", "I", "I", "I", "O", "T", "I", "O", "T", "I", "I", "I", "T", "O");
		
		Pair pair = new Pair (sentence, iob);
		return pair.getTokenized_();
	}
	
	public List<String> output() {
		List<String> tokens = Arrays.asList("I", "'m", "not", "so", "sure", "that",
				"this", "will", "work", ",", "but", "let", "'s", "hope", "it", "does", ".");
		return tokens;
	}
	
	@Test
	public void testTokenizing() {
		List<String> input = input();
		Iterator<String> inputIt = input.iterator();
		List<String> output = output();
		Iterator<String> outputIt = output.iterator();
		
		while(inputIt.hasNext() && outputIt.hasNext()) {
			assertEquals(outputIt.next(), inputIt.next());
		}
	}
}

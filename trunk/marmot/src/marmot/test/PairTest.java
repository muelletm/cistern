// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import marmot.tokenize.Pair;

import org.junit.Test;

public class PairTest {

	public String untokenized() {
		return "I'm not so sure that this will work, but let's hope it does. "; 
	}
	
	public List<String> iob() {
		return Arrays.asList("T", "T", "I", "O", "T", "I", "I", "O", 
				"T", "I", "O", "T", "I", "I", "I", "O", "T", "I", "I", "I", "O", 
				"T", "I", "I", "I", "O", "T", "I", "I", "I", "O", "T", "I", "I", 
				"I", "T", "O", "T", "I", "I", "O", "T", "I", "I", "T", "I", "O", 
				"T", "I", "I", "I", "O", "T", "I", "O", "T", "I", "I", "I", "T", "O");
	}
	
	public List<String> tokenized() {
		List<String> tokens = Arrays.asList("I", "'m", "not", "so", "sure", "that",
				"this", "will", "work", ",", "but", "let", "'s", "hope", "it", "does", ".");
		return tokens;
	}
	
	@Test
	public void testFromTags() {
		
		String sentence = untokenized();
		List<String> iob = iob();
		
		Pair pair = Pair.fromTags(sentence, iob);		
		
		List<String> actual = pair.getTokenized();
		List<String> expected = tokenized();
		
		assertEquals(expected, actual);		
	}
	
	@Test
	public void testFromTokenized() {

		String sentence = untokenized();
		List<String> tokenized = tokenized();

		Pair pair = Pair.fromTokenized(sentence, tokenized);

		List<String> actual = pair.getTags();
		List<String> expected = iob();

		assertEquals(expected, actual);
	}
}

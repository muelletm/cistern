// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.morph.io.Tokenizer;

import org.junit.Test;


public class TokenizerTest {

	public List<Sentence> getInputSentences() {
		List<Sentence> sentences = new LinkedList<Sentence>();
		List<Word> tokens;

		tokens = new LinkedList<Word>();
		tokens.add(new Word("-LRB-", "(", "_"));
		tokens.add(new Word("das", "A", "c=N|n=S"));
		tokens.add(new Word("ist", "V", "n=S"));
		tokens.add(new Word("ein", "A", "c=N|n=S"));
		tokens.add(new Word("Unit-Test", "N", "c=N|n=S"));
		tokens.add(new Word("...", ".", "_"));
		tokens.add(new Word("-RRB-", ")", "_"));
		sentences.add(new Sentence(tokens));
		
		tokens = new LinkedList<Word>();
		tokens.add(new Word("_", ""));
		tokens.add(new Word("_das", ""));
		tokens.add(new Word("ist_", ""));
		tokens.add(new Word("e_i_n", ""));
		tokens.add(new Word("Test_", ""));
		sentences.add(new Sentence(tokens));
		

		return sentences;
	}
	
	public List<Sentence> getOutputSentences() {
		List<Sentence> sentences = new LinkedList<Sentence>();
		List<Word> tokens;

		tokens = new LinkedList<Word>();
		tokens.add(new Word("(", "B|(", "_"));
		tokens.add(new Word("das", "B|A", "c=N|n=S"));
		tokens.add(new Word("ist", "B|V", "n=S"));
		tokens.add(new Word("ein", "B|A", "c=N|n=S"));
		tokens.add(new Word("Unit", "B|N", "c=N|n=S"));
		tokens.add(new Word("-", "I|N", "c=N|n=S"));
		tokens.add(new Word("Test", "I|N", "c=N|n=S"));
		tokens.add(new Word(".", "B|.", "_"));
		tokens.add(new Word(".", "I|.", "_"));
		tokens.add(new Word(".", "I|.", "_"));
		tokens.add(new Word(")", "B|)", "_"));
		sentences.add(new Sentence(tokens));
		
		tokens = new LinkedList<Word>();
		tokens.add(new Word("das", "B|"));
		tokens.add(new Word("ist", "B|"));
		tokens.add(new Word("e", "B|"));
		tokens.add(new Word("i", "I|"));
		tokens.add(new Word("n", "I|"));
		tokens.add(new Word("Test", "B|"));
		sentences.add(new Sentence(tokens));

		return sentences;
	}
	
	public List<Sentence> getOutputBetweenSentences() {
		List<Sentence> sentences = new LinkedList<Sentence>();
		List<Word> tokens;

		tokens = new LinkedList<Word>();
		tokens.add(new Word("(", "B|(", "_"));
		tokens.add(new Word("das", "B|A", "c=N|n=S"));
		tokens.add(new Word("ist", "B|V", "n=S"));
		tokens.add(new Word("ein", "B|A", "c=N|n=S"));
		tokens.add(new Word("Unit-Test", "B|N", "c=N|n=S"));
		tokens.add(new Word(".", "B|.", "_"));
		tokens.add(new Word(".", "I|.", "_"));
		tokens.add(new Word(".", "I|.", "_"));
		tokens.add(new Word(")", "B|)", "_"));
		sentences.add(new Sentence(tokens));
		
		tokens = new LinkedList<Word>();
		tokens.add(new Word("_", "B|", null));
		tokens.add(new Word("_", "B|", null));
		tokens.add(new Word("das", "I|", null));
		tokens.add(new Word("ist", "B|", null));
		tokens.add(new Word("_", "I|", null));
		tokens.add(new Word("e_i_n", "B|", null));
		tokens.add(new Word("Test", "B|", null));
		tokens.add(new Word("_", "I|", null));
		sentences.add(new Sentence(tokens));

		return sentences;
	}

	
	@Test
	public void test() {
		List<Sentence> input_sentences = getInputSentences();
		List<Sentence> output_sentences = getOutputSentences();
		
		for (int i = 0; i< input_sentences.size(); i++) {
			Tokenizer tokenizer = new Tokenizer(false, true);
			Sentence actual_sentence = tokenizer.overTokenize(input_sentences.get(i), true);
			assertEqual(actual_sentence, output_sentences.get(i));
		}
		
	}
	
	@Test
	public void testInBetween() {
		List<Sentence> input_sentences = getInputSentences();
		List<Sentence> output_sentences = getOutputBetweenSentences();
		
		for (int i = 0; i< input_sentences.size(); i++) {
			Tokenizer tokenizer = new Tokenizer(true, false);
			Sentence actual_sentence = tokenizer.overTokenize(input_sentences.get(i), true);
			
			assertEqual(actual_sentence, output_sentences.get(i));
		}
		
	}
	
	@Test
	public void plainTextTokenizer() {
		String sentence = "Das ist ein Test.";
		Tokenizer tokenizer = new Tokenizer(true, true);

		List<Word> tokens;

		tokens = new LinkedList<Word>();

		tokens.add(new Word("Das", null, null ));
		tokens.add(new Word("ist", null, null ));
		tokens.add(new Word("ein", null, null ));
		tokens.add(new Word("Test", null, null ));
		tokens.add(new Word(".", null, null ));
		
		sentence = " Das ist ein Test. ";
		assertEqual(tokenizer.tokenize(sentence), new Sentence(tokens));
		
		sentence = " _Das ___ _ist ein Test. _";
		assertEqual(tokenizer.tokenize(sentence), new Sentence(tokens));

		
	}
	
	private void assertEqual(Sentence actual_sentence, Sentence sentence) {
		assertEquals(actual_sentence.size(), sentence.size());
		for (int i = 0; i< sentence.size(); i++) {
			
			boolean equals = sentence.getWord(i).equals(actual_sentence.getWord(i)); 
			
			if (!equals) {
				System.err.println(sentence.getWord(i));
				System.err.println(actual_sentence.getWord(i));
			}
			
			
			
			assertTrue(equals);
		}
	}

}

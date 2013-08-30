// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.morph.io.Tokenizer;

import org.junit.Test;


public class TokenizerTest {

	public List<Sentence> getInputSentences() {
		List<Sentence> sentences = new LinkedList<Sentence>();
		List<Word> tokens;

		tokens = new LinkedList<Word>();
		tokens.add(new Word("-LRB-", "(", "_", null));
		tokens.add(new Word("das", "A", "c=N|n=S", null));
		tokens.add(new Word("ist", "V", "n=S", null));
		tokens.add(new Word("ein", "A", "c=N|n=S", null));
		tokens.add(new Word("Unit-Test", "N", "c=N|n=S", null));
		tokens.add(new Word("...", ".", "_", null));
		tokens.add(new Word("-RRB-", ")", "_", null));
		sentences.add(new Sentence(tokens));
		
		tokens = new LinkedList<Word>();
		tokens.add(new Word("_", "", null, null));
		tokens.add(new Word("_das", "", null, null));
		tokens.add(new Word("ist_", "", null, null));
		tokens.add(new Word("e_i_n", "", null, null));
		tokens.add(new Word("Test_", "", null, null));
		sentences.add(new Sentence(tokens));
		

		return sentences;
	}
	
	public List<Sentence> getOutputSentences() {
		List<Sentence> sentences = new LinkedList<Sentence>();
		List<Word> tokens;

		tokens = new LinkedList<Word>();
		tokens.add(new Word("(", "O|(", "_", null));
		tokens.add(new Word("das", "O|A", "c=N|n=S", null));
		tokens.add(new Word("ist", "O|V", "n=S", null));
		tokens.add(new Word("ein", "O|A", "c=N|n=S", null));
		tokens.add(new Word("Unit", "B|N", "c=N|n=S", null));
		tokens.add(new Word("-", "I|N", "c=N|n=S", null));
		tokens.add(new Word("Test", "I|N", "c=N|n=S", null));
		tokens.add(new Word(".", "B|.", "_", null));
		tokens.add(new Word(".", "I|.", "_", null));
		tokens.add(new Word(".", "I|.", "_", null));
		tokens.add(new Word(")", "O|)", "_", null));
		sentences.add(new Sentence(tokens));
		
		tokens = new LinkedList<Word>();
		tokens.add(new Word("_", "O|", null, null));
		tokens.add(new Word("_", "B|", null, null));
		tokens.add(new Word("das", "I|", null, null));
		tokens.add(new Word("ist", "B|", null, null));
		tokens.add(new Word("_", "I|", null, null));
		tokens.add(new Word("e", "B|", null, null));
		tokens.add(new Word("i", "I|", null, null));
		tokens.add(new Word("n", "I|", null, null));
		tokens.add(new Word("Test", "B|", null, null));
		tokens.add(new Word("_", "I|", null, null));
		sentences.add(new Sentence(tokens));

		return sentences;
	}

	
	@Test
	public void test() {
		List<Sentence> input_sentences = getInputSentences();
		List<Sentence> output_sentences = getOutputSentences();
		
		for (int i = 0; i< input_sentences.size(); i++) {
			Sentence actual_sentence = Tokenizer.overTokenize(input_sentences.get(i));
			
			assertEqual(actual_sentence, output_sentences.get(i));
		}
		
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

// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.morph.io;

import java.util.List;

import marmot.core.Sequence;
import marmot.morph.io.SentenceTokenizer;
import marmot.test.morph.PipelineTest;

import org.junit.Test;


public class SentenceTokenizerTest {



	@Test
	public void test() {
		PipelineTest ptest = new PipelineTest();
		
		List<Sequence> sentences = ptest.getSentences("form-index=1,tag-index=4,morph-index=6,trn.txt", 100);
		
		System.err.println("num tokens: " + getNumTokens(sentences));
		
		List<Sequence> actual_sentences = SentenceTokenizer.resegment(sentences, 100);
		
//		for (Sequence actual_sentence : actual_sentences) {
//			for (Token token : actual_sentence) {
//				Word word = (Word) token; 
//				System.out.format("%s\t%s\t%s\n", word.getWordForm(), word.getPosTag(), word.getMorphTag());
//			}
//			System.out.println();
//		}
		
		System.err.println(sentences.size());
		System.err.println(actual_sentences.size());
		
	}

	private int getNumTokens(List<Sequence> sentences) {
		int num_tokens = 0;
		for (Sequence sentence : sentences) {
			num_tokens += sentence.size();
		}
		return num_tokens;
	}

}

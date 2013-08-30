// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.core.Token;
import marmot.morph.Sentence;
import marmot.morph.Word;
import marmot.util.MutableBoolean;


public class Tokenizer {

	private final static Map<String,String> token_map_ = new HashMap<String, String>();
	static {
		token_map_.put("-LCB-", "{");
		token_map_.put("-RCB-", "}");
		token_map_.put("-LRB-", "(");
		token_map_.put("-RRB-", ")");
		token_map_.put("-LSB-", "[");
		token_map_.put("-RSB-", "]");
	}
	
	public static Sentence overTokenize(Sentence sentence) {
		List<Word> words = new LinkedList<Word>();
		
		for (Token token : sentence) {
			Word word = (Word) token;
			overTokenize(word, words);
		}
		
		return new Sentence(words);
	}
	
	public static void overTokenize(Word word, List<Word> words) {
		word = replaceWord(word);
			
		String word_form = word.getWordForm();
		
		if (word_form.length() == 1) {
			words.add(createSingleton(word));
			return;
		}
			
		StringBuilder sb = new StringBuilder(word.getWordForm().length());
		MutableBoolean first = new MutableBoolean(true);
		
		for (int i = 0; i < word_form.length(); i++) {
			char c = word_form.charAt(i);
			
			if (Character.isDigit(c) || Character.isLetter(c)) {
				sb.append(c);
			} else {
				if (sb.length() > 0) {
					words.add(createWord(word, sb, first));
				}
				
				if (!weirdUnderScoreThing(c, i, word_form)) {
					sb.append(c);
					words.add(createWord(word, sb, first));
				}
			}
		}
		
		if (sb.length() > 0) {			
			if (sb.length() == word_form.length()) {
				words.add(createSingleton(word));
			} else {
				words.add(createWord(word, sb, first));
			}
		}
	}
	
	private static Word replaceWord(Word word) {
		String replacement = token_map_.get(word.getWordForm());
		if (replacement != null) {
			word = word.shallowCopy();
			word.setWordForm(replacement);
		}
		return word;
	}

	private static Word createSingleton(Word word) {
		Word new_word = word.shallowCopy();		
		new_word.setPosTag("O" + "|" + new_word.getPosTag() );
		return new_word;
	}

	private static boolean weirdUnderScoreThing(char c, int i, String word_form) {
		if (c != '_')
			return false;
		
		if (i - 1 < 0) {
			return false;
		}
		
		c = word_form.charAt(i - 1);
		if (!(Character.isDigit(c) || Character.isLetter(c)))
			return false;
		
		if (i + 1 >= word_form.length())
			return false;
		
		c = word_form.charAt(i + 1);
		if (!(Character.isDigit(c) || Character.isLetter(c)))
			return false;
		
		return true;
	}

	private static Word createWord(Word word, StringBuilder sb, MutableBoolean first) {
		Word new_word = word.shallowCopy();
		
		new_word.setWordForm(sb.toString());
		sb.setLength(0);
		
		if (new_word.getPosTag() != null) {
			String prefix = "I";
			if (first.get()) {
				prefix = "B";
				first.set(false);
			}
			new_word.setPosTag(prefix + "|" + new_word.getPosTag());
		}
		
		return new_word;
	}
	
}

// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.io;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Sentence extends AbstractList<Token> implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Token> tokens_;

	public Sentence(List<Token> tokens) {
		tokens_ = new ArrayList<Token>(tokens);
	}

	public Sentence(List<String> words, List<String> tags) {
		tokens_ = new ArrayList<Token>(words.size());
		for (int token_index = 0; token_index < words.size(); token_index++) {
			tokens_.add(new Token(words.get(token_index), tags.get(token_index)));
		}
	}

	public Sentence(Sentence sentence) {
		tokens_ = new ArrayList<Token>(sentence.tokens_);
	}

	public Sentence(String sentence_string) {
		this(sentence_string, "\\s+", "_");
	}

	public Sentence(String sentence_string, String token_delimiter,
			String form_tag_delimiter) {
		String[] tokens = sentence_string.split(token_delimiter);

		tokens_ = new ArrayList<Token>(tokens.length);
		for (String token_string : tokens) {
			String[] form_tag = token_string.split(form_tag_delimiter);

			Token token;
			if (form_tag.length == 1) {
				token = new Token(form_tag[0], null);
			} else if (form_tag.length == 2) {
				token = new Token(form_tag[0], form_tag[1]);
			} else {
				throw new RuntimeException("More than one from/tag delimiter: "
						+ token_string);
			}
			
			tokens_.add(token);
		}

	}

	@Override
	public Token get(int index) {
		return tokens_.get(index);
	}

	@Override
	public int size() {
		return tokens_.size();
	}

	public void setTags(List<String> tags) {
		assert tags.size() == size();
		for (int index = 0; index < size(); index++) {
			tokens_.get(index).setTag(tags.get(index));
		}
	}
	
	@Override 
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
				
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof Sentence)) {
			return false;
		}
		
		Sentence other_sentence = (Sentence) other;
		
		if (other_sentence.size() != size()) {
			return false;
		}
		
		for (int index = 0; index < size(); index++) {
			if (!tokens_.get(index).equals(other_sentence.get(index))) {
				return false;
			}
		}
		
		return true;
	}
	
}

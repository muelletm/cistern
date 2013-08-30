// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import marmot.core.Sequence;
import marmot.core.Token;



public class Sentence extends AbstractList<Token> implements Sequence {

	private static final long serialVersionUID = 1L;
	private List<Word> tokens_;

	public Sentence(List<Word> tokens) {
		tokens_ = new ArrayList<Word>(tokens);
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
			tokens_.get(index).setPosTag(tags.get(index));
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
		
		if (!(other instanceof Sequence)) {
			return false;
		}
		
		Sequence other_sentence = (Sequence) other;
		
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

	public Word getWord(int i) {
		return (Word) get(i);
	}
	
	
}

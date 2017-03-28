// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import marmot.core.Sequence;
import marmot.core.Token;



public class Sentence extends AbstractList<Token> implements Sequence {

	private static final long serialVersionUID = 1L;
	private List<Word> tokens_;
	private List<String> comments_;
	private Hashtable<Integer, String> segments_;
	private Hashtable<Integer, String> empty_nodes_;

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

	public void setEmptyNodes(Hashtable<Integer, String> e) {
		empty_nodes_ = e; 
	}

	public Hashtable<Integer, String> getEmptyNodes() {
		return empty_nodes_;
	}

	public void setSegments(Hashtable<Integer, String> s) {
		segments_ = s; 
	}

	public Hashtable<Integer, String> getSegments() {
		return segments_;
	}

	public void setComments(List<String> c) {
		comments_ = c; 
	}

	public List<String> getComments() {
		return comments_;
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

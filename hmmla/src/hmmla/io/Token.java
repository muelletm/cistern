// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.io;

import java.io.Serializable;

public class Token implements Serializable {
	private static final long serialVersionUID = 1L;
	private String word_;
	private String tag_;
	
	public Token(String word, String tag) {
		word_ = word;
		tag_ = tag;
	}

	public String getWordForm() {
		return word_;
	}
	
	public String getTag() {
		return tag_;
	}

	public void setTag(String tag) {
		tag_ = tag;
	}
	
	@Override 
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
				
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof Token)) {
			return false;
		}
		
		Token other_token = (Token) other;
		
		if (!word_.equals(other_token.getWordForm())) {
			return false;
		}
		
		if (tag_ == null) {
			if (other_token.getTag() == null) {
				return true;
			} 
			return false;
		}
				
		return tag_.equals(other_token.getTag());
	}
	
	@Override
	public String toString() {
		return word_ + " " + tag_;
	}
	
}

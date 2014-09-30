// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize;

import java.util.ArrayList;
import java.util.List;

public class Pair {

	private String untokenized_;
	private List<String> tokenized_;
	private List<String> tags_;
	
	public Pair(String untokenized, List<String> tokenized, List<String> tags) {
		untokenized_ = untokenized;
		tokenized_ = tokenized;
		tags_ = tags;
	}
	
	public static Pair fromTags(String untokenized, List<String> tags) {		
		List<String> tokenized = getTokenized(untokenized, tags);
		return new Pair(untokenized, tokenized, tags);
	}

	private static List<String> getTokenized(String untokenized, List<String> tags) {
		List<String> tokenized_ = new ArrayList<>(tags.size());
		
		int start = 0;
		int end = 0;
		int ws = 0;

		for(String s : tags) {
			if(s.equals("O")) {
				ws++;
			} else {
				if(s.equals("T")) {
					if(start!=end) {
						tokenized_.add(untokenized.substring(start, end));
						end += ws;
						start = end;
						ws = 0;
					}
				}
				end++;
			}
		}
		
		if(start!=end) {
			tokenized_.add(untokenized.substring(start, end));
		}
		
		return tokenized_;
	}

	@Override
	public String toString() {
		return untokenized_ + " " + tokenized_.toString() + " " + tags_.toString();
	}

	public String getUntokenized() {
		return untokenized_;
	}

	public List<String> getTokenized() {
		return tokenized_;
	}

	public List<String> getTags() {
		return tags_;
	}
	
}

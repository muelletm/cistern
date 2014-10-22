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
	
	public static Pair fromTokenized(String untokenized, List<String> tokenized) {
		List<String> tags = getTags(untokenized, tokenized);
		return new Pair(untokenized, tokenized, tags);
	}

	private static List<String> getTokenized(String untokenized, List<String> tags) {
		List<String> tokenized_ = new ArrayList<String>(tags.size());
		
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
	
	private static List<String> getTags(String untokenized, List<String> tokenized) {
		List<String> tags_ = new ArrayList<String>(tokenized.size());
		
		int tmp = 0;
		
		for(String s : tokenized) {
			tags_.add("T");
			tmp++;
			for(int i=0; i < s.length() -1; i++) {
				tags_.add("I");
				tmp++;
			}
			
			while( (tmp<untokenized.length()) && untokenized.charAt(tmp) == ' ') {
			    tags_.add("O");
			    tmp++;
			}
		}
		
		return tags_;
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

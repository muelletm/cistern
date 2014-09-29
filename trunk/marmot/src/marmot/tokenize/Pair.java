package marmot.tokenize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pair {

	private String untokenized_;
	private List<String> tokenized_;
	private List<String> tags_;
	
	Pair(String untokenized, String[] tags) {
		this(untokenized, Arrays.asList(tags));
	}
	
	public Pair(String untokenized, List<String> tags) {
		untokenized_ = untokenized;
		tags_ = new ArrayList<>(tags);
		setTokenized_();
	}

	private void setTokenized_() {
		tokenized_ = new ArrayList<>(tags_.size());
		
		int start = 0;
		int end = 0;
		int ws = 0;

		for(String s : tags_) {
			if(s.equals("O")) {
				ws++;
			} else {
				if(s.equals("T")) {
					if(start!=end) {
						tokenized_.add(untokenized_.substring(start, end));
						end += ws;
						start = end;
						ws = 0;
					}
				}
				end++;
			}
		}
		
		if(start!=end) {
			tokenized_.add(untokenized_.substring(start, end));
		}
	}

	@Override
	public String toString() {
		return untokenized_ + " " + tokenized_.toString() + " " + tags_.toString();
	}

	public String getUntokenized_() {
		return untokenized_;
	}

	public List<String> getTokenized_() {
		return tokenized_;
	}

	public List<String> getTags_() {
		return tags_;
	}
	
}

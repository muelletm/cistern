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
		tags_ = new ArrayList<>();
		setTokenized_();
	}

	private void setTokenized_() {
		tokenized_ = new ArrayList<>(tags_.size());
		
		// TODO: Finish implementation of setTokenized!
		// Parse the tag sequence and create the tokenized text from the untokenized string.
		
	}

	@Override
	public String toString() {
		return untokenized_ + " " + tokenized_.toString() + " " + tags_.toString();
	}
	
}

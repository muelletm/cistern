package marmot.tokenize;

import java.util.Arrays;
import java.util.List;

public class Pair {

	private String untokenized_;
	private List<String> tokenized_;
	
	Pair(String untokenized, String[] tokenized) {
		this(untokenized, Arrays.asList(tokenized));
	}
	
	public Pair(String untokenized, List<String> tokenized) {
		untokenized_ = untokenized;
		tokenized_ = tokenized;
	}

	@Override
	public String toString() {
		return untokenized_ + " " + tokenized_.toString();
	}
	
}

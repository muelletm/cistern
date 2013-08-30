// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util.eval;



public class TaggingScorer extends AbstractOneTokenPerLineScorer {

	private int gold_tag_index_ = 1;
	private int predicted_tag_index_ = 1;

	@Override
	public double getScore(String[] actual_tokens, String[] expected_tokens) {	
		String actual_tag = actual_tokens[gold_tag_index_];
		
		boolean tag_correct = actual_tag.equals(expected_tokens[predicted_tag_index_]);
		if (tag_correct) {
			return 1.0;
		}
		return 0.0;
	}

}

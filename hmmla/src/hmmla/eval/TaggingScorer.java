// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.eval;

import hmmla.util.Mapping;

public class TaggingScorer extends AbstractOneTokenPerLineScorer {

	private int gold_tag_index_ = 4;
	private int predicted_tag_index_ = 2;
	private Mapping mapper_;

	@Override
	public void setOption(String option, String value) {
		
		if (option.equalsIgnoreCase("upos")) {
			mapper_ = new Mapping(value);
		} else {
			throw new RuntimeException("Unknown option: " +option);
		}
		
	}
	
	@Override
	public double getScore(String[] actual_tokens, String[] expected_tokens) {	
		String actual_tag = actual_tokens[gold_tag_index_];
		
		if (mapper_ != null) {
			actual_tag = mapper_.get(actual_tag);
		}
		
		boolean tag_correct = actual_tag.equals(expected_tokens[predicted_tag_index_]);
		if (tag_correct) {
			return 1.0;
		}
		return 0.0;
	}

}

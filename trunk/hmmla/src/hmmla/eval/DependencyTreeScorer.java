// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.eval;

public class DependencyTreeScorer extends AbstractOneTokenPerLineScorer {

	private final int gold_head_index_ = 8;
	private final int gold_label_index_ = 10;
	private final int predicted_head_index_ = 9;
	private final int predicted_label_index_ = 11;

	@Override
	public double getScore(String[] actual_tokens, String[] predicted_tokens) {
		boolean head_correct = actual_tokens[gold_head_index_]
				.equals(predicted_tokens[predicted_head_index_]);
		
		boolean label_correct = actual_tokens[gold_label_index_]
				.equals(predicted_tokens[predicted_label_index_]); 
		
		if (head_correct && label_correct) {
			return 1.;
		}
		return 0.;
	}
}

// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class SimpleHmmModel implements HmmModel {
	protected Model model_;
	private Statistics normalized_stats_;

	public SimpleHmmModel(Statistics normalized_stats, Model model) {
		model_ = model;
		normalized_stats_ = normalized_stats;
	}

	@Override
	public void getTransitions(int i, double[] scores) {
		for (int j = 0; j < scores.length; j++) {
			scores[j] = getTransitions(i, j);
		}
	}
	
	public double getEmissions(int i, int t) {
		double f = normalized_stats_.getEmissions(i, t);		
		return f;
	}

	@Override
	public void getEmissions(String word, double[] scores) {
		try {
			int index = model_.getWordTable().toIndex(word);
			for (int j = 0; j < scores.length; j++) {
				scores[j] = getEmissions(j, index);
			}
		} catch (NoSuchElementException e) {
			Arrays.fill(scores, 0.0);
		}
		scores[Model.BorderIndex] = Double.NEGATIVE_INFINITY;
	}

	@Override
	public double getTransitions(int i, int j) {
		double f = normalized_stats_.getTransitions(i, j);
		return f;
	}
	
	public Statistics getStatistics() {
		return normalized_stats_;
	}
	
}

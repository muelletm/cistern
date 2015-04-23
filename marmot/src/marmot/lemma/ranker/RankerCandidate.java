// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.ranker;

import java.util.List;

import marmot.core.State;
import marmot.core.WeightVector;
import marmot.lemma.LemmaCandidate;
import marmot.morph.MorphModel;
import marmot.morph.MorphWeightVector;

public class RankerCandidate {

	private boolean correct_;
	private LemmaCandidate candidate_;
	private String lemma_;
	private double score_;
	private double expected_counts_;
	
	public RankerCandidate(String lemma, LemmaCandidate candidate, boolean correct, double score) {
		score_ = score;
		candidate_ = candidate;
		correct_ = correct;
		lemma_ = lemma;
	}

	public double getScore() {
		return score_;
	}

	public LemmaCandidate getCandidate() {
		return candidate_;
	}

	public boolean isCorrect() {
		return correct_;
	}

	public void update(State state, WeightVector weights, double value) {
		MorphWeightVector mweights = (MorphWeightVector) weights;
		MorphModel morph_model = mweights.getModel();
		RankerModel model = morph_model.getLemmaModel();
		
		assert state.getOrder() == 1;
		assert state.getLevel() < 2;
		
		int pos_index;
		int[] morph_indexes;
		
		if (state.getLevel() == 0) {
			pos_index = state.getIndex();
			morph_indexes = RankerInstance.EMPTY_ARRAY;
		} else {
			pos_index = state.getSubLevelState().getIndex();
			int morph_index = state.getIndex();
			morph_indexes = morph_model.getTagToSubTags()[1][morph_index];
			if (morph_indexes == null)
				morph_indexes = RankerInstance.EMPTY_ARRAY;
			if (!morph_model.getLemmaUseMorph()) {
				morph_indexes = RankerInstance.EMPTY_ARRAY;
			}
			
		}
		
		assert pos_index < morph_model.getTagTables().get(0).size();
		model.update(candidate_, pos_index, morph_indexes, value);
	}

	public void incrementEstimatedCounts(double expected_counts) {
		expected_counts_ += expected_counts;
	}

	public void updateWeights(State state, WeightVector weights) {
		if (expected_counts_ != 0.0) {
			update(state, weights, expected_counts_);
			expected_counts_ = 0.0;
		}
	}

	public static RankerCandidate bestCandidate(List<RankerCandidate> lemma_candidates) {
		assert lemma_candidates != null;
		assert !lemma_candidates.isEmpty();
		
		RankerCandidate best_candidate = null; 
		
		for (RankerCandidate candidate : lemma_candidates) {
			if (best_candidate == null || best_candidate.getScore() < candidate.getScore()) {
				best_candidate = candidate;
			}
		}
		
		assert best_candidate != null;
		return best_candidate;
	}

	public String getLemma() {
		return lemma_;
	}
	
}

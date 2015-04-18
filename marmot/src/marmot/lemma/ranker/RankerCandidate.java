package marmot.lemma.ranker;

import marmot.core.State;
import marmot.core.WeightVector;
import marmot.lemma.LemmaCandidate;
import marmot.morph.MorphModel;
import marmot.morph.MorphWeightVector;

public class RankerCandidate {

	private boolean correct_;
	private LemmaCandidate candidate_;
	private double score_;
	private double expected_counts_;
	
	public RankerCandidate(LemmaCandidate candidate, boolean correct, double score) {
		score_ = score;
		candidate_ = candidate;
		correct_ = correct;
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
		}
		
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
	
}

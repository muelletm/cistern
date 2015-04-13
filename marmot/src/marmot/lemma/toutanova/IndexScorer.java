package marmot.lemma.toutanova;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.SymbolTable;

public class IndexScorer extends IndexConsumer {

	public IndexScorer(DynamicWeights weights,
			SymbolTable<Feature> feature_map, int num_pos_bits) {
		super(weights, feature_map, num_pos_bits);
	}

	double score_;
	
	public void reset() {
		score_ = 0.0;
	}
	
	@Override
	public void consume(int index) {
		if (index >= 0) {
			score_ += weights_.get(index);
		}
	}

	public double getScore() {
		return score_;
	}

	@Override
	protected boolean getInsert() {
		return false;
	}

}

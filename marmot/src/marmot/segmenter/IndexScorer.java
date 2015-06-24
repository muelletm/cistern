// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.segmenter;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.SymbolTable;

public class IndexScorer extends IndexConsumer {

	private static final long serialVersionUID = 1L;

	public IndexScorer(DynamicWeights weights,
			SymbolTable<Feature> feature_map, int num_tag_bits) {
		super(weights, feature_map, true, num_tag_bits);
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

}

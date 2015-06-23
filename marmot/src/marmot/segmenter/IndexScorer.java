// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.segmenter;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.SymbolTable;

public class IndexScorer extends IndexConsumer {

	private static final long serialVersionUID = 1L;
	private boolean insert_;

	public IndexScorer(DynamicWeights weights,
			SymbolTable<Feature> feature_map) {
		super(weights, feature_map);
		insert_ = true;
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
		return insert_;
	}

	public void setInsert(boolean insert) {
		insert_ = insert;
	}

}

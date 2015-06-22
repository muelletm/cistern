// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.segmenter;

import java.io.Serializable;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public abstract class IndexConsumer implements Serializable {

	private static final long serialVersionUID = 1L;
	private SymbolTable<Feature> feature_map_;
	protected DynamicWeights weights_;

	public abstract void consume(int index);

	public IndexConsumer(DynamicWeights weights, SymbolTable<Feature> feature_map) {
		setWeights(weights);
		feature_map_ = feature_map;
	}
	
	public void consume(SegmentationInstance instance, Encoder encoder) {
		int index = feature_map_.toIndex(encoder.getFeature(), -1, getInsert());
		consume(index);
	}
	
	protected abstract boolean getInsert();

	public void setWeights(DynamicWeights weights) {
		weights_ = weights;
	}
	
}

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
	private boolean insert_;

	public abstract void consume(int index);

	public IndexConsumer(DynamicWeights weights, SymbolTable<Feature> feature_map, boolean insert) {
		setWeights(weights);
		feature_map_ = feature_map;
		insert_ = insert;
	}
	
	public void consume(Encoder encoder) {
		int index = feature_map_.toIndex(encoder.getFeature(), -1, getInsert());
		consume(index);
	}
	
	public void setWeights(DynamicWeights weights) {
		weights_ = weights;
	}
	
	public DynamicWeights getWeights() {
		return weights_;
	}
	
	public SymbolTable<Feature> getFeatureTable() {
		return feature_map_;
	}
	
	protected boolean getInsert() {
		return insert_;
	}

	public void setInsert(boolean insert) {
		insert_ = insert;
	}
}

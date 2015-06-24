// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.segmenter;

import java.io.Serializable;
import java.util.List;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public abstract class IndexConsumer implements Serializable {

	private static final long serialVersionUID = 1L;
	private SymbolTable<Feature> feature_map_;
	protected DynamicWeights weights_;
	private boolean insert_;
	private int num_tags_bits_;
	private Encoder.State state_;
	
	public abstract void consume(int index);

	public IndexConsumer(DynamicWeights weights, SymbolTable<Feature> feature_map, boolean insert, int num_tag_bits) {
		setWeights(weights);
		feature_map_ = feature_map;
		insert_ = insert;
		num_tags_bits_ = num_tag_bits;
		state_ = new Encoder.State();
	}
	
	public void consume(Encoder encoder, List<Integer> tags) {
		encoder.storeState(state_);
		for (int tag : tags) {
			encoder.append(tag, num_tags_bits_);
			int index = feature_map_.toIndex(encoder.getFeature(), -1, getInsert());
			consume(index);
			encoder.restoreState(state_);
		}
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

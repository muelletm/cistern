// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package chipmunk.segmenter;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.SymbolTable;

public class IndexUpdater extends IndexConsumer {

	private static final long serialVersionUID = 1L;

	public IndexUpdater(DynamicWeights weights,
			SymbolTable<Feature> feature_map, int num_tag_bits) {
		super(weights, feature_map, true, num_tag_bits);
	}

	private double update_;
	
	public void setUpdate(double update) {
		update_ = update;
	}
	
	@Override
	public void consume(int index) {
		assert index >= 0;
		weights_.increment(index, update_);
	}

}

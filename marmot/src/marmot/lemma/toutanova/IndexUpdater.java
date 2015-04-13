package marmot.lemma.toutanova;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.SymbolTable;

public class IndexUpdater extends IndexConsumer {

	public IndexUpdater(DynamicWeights weights,
			SymbolTable<Feature> feature_map, int num_pos_bits) {
		super(weights, feature_map, num_pos_bits);
	}

	private double update_;
	
	public void setUpdate(double update) {
		update_ = update;
	}
	
	@Override
	public void consume(int index) {
		if (index >= 0) {
			weights_.incrementWeight(index, update_);
		}
	}

	@Override
	protected boolean getInsert() {
		return true;
	}

}

package marmot.util;

import marmot.core.Feature;

public class ExactFeatureTable implements FeatureTable {

	private static final long serialVersionUID = 1L;
	private SymbolTable<Feature> table_;
	private transient Feature feature_;
	
	public ExactFeatureTable() {
		table_ = new SymbolTable<>();
	}
	
	@Override
	public int size() {
		return table_.size();
	}
	
	@Override
	public int getFeatureIndex(Encoder encoder, boolean insert) {
		if (feature_ == null) {
			feature_ = new Feature(encoder.getCapacity());
		}
		
		encoder.copyToFeature(feature_);
		int index = table_.toIndex(feature_, -1, false);
		if (index >= 0)
			return index;
		if (insert) {
			index = table_.toIndex(feature_, true);
			feature_ = null;
		}
		return index;
	}
	
}

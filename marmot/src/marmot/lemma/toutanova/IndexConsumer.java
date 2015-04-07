package marmot.lemma.toutanova;

import marmot.core.Feature;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public abstract class IndexConsumer {

	private SymbolTable<Feature> feature_map_;
	private int num_pos_bits_;

	public abstract void consume(int index);

	public void consume(ToutanovaInstance instance, Encoder encoder) {
		int index = feature_map_.toIndex(encoder.getFeature(), -1, getInsert());
		consume(index);
		
		if (num_pos_bits_ >= 0) {
			encoder.append(instance.getPosTagIndex(), num_pos_bits_);
			index = feature_map_.toIndex(encoder.getFeature(), -1, getInsert());
			consume(index);
		}
	}
	
	protected abstract boolean getInsert();

	public IndexConsumer setFeatureMap(SymbolTable<Feature> feature_map) {
		feature_map_ = feature_map;
		return this;
	}
	
	public IndexConsumer setPosBits(int num_pos_bits) {
		num_pos_bits_ = num_pos_bits;
		return this;
	}
	
}

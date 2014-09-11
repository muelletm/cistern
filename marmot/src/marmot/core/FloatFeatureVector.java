package marmot.core;

import java.io.Serializable;

public interface FloatFeatureVector extends Serializable {
	
	public int getDim();
	public void updateFloatWeight(FloatWeights weights, int tag_index, int offset, double update);
	public double getDotProduct(FloatWeights weights, int tag_index, int offset);
	
}

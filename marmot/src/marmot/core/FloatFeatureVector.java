// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.io.Serializable;

public interface FloatFeatureVector extends Serializable {
	
	public int getDim();
	public void updateFloatWeight(FloatWeights weights, int tag_index, int offset, double update);
	public double getDotProduct(FloatWeights weights, int tag_index, int offset);
	
}

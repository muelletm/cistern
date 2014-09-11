// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

public class ConcatFloatFeatureVector implements FloatFeatureVector {

	private static final long serialVersionUID = 1L;
	private FloatFeatureVector vec_;
	private FloatFeatureVector vec2_;
	private int dim_;

	public ConcatFloatFeatureVector(FloatFeatureVector vec,
			FloatFeatureVector vec2) {
			vec_ = vec;
			vec2_ = vec2;
			dim_ = vec.getDim() + vec2.getDim();
	}

	@Override
	public int getDim() {
		return dim_;
	}

	@Override
	public void updateFloatWeight(FloatWeights weights,
			int tag_index, int offset, double update) {
		vec_.updateFloatWeight(weights, tag_index, offset, update);
		vec2_.updateFloatWeight(weights, tag_index, offset + vec_.getDim(), update);
	}

	@Override
	public double getDotProduct(FloatWeights weights, int tag_index, int offset) {
		return vec_.getDotProduct(weights, tag_index, offset) 
				+ vec2_.getDotProduct(weights, tag_index, offset + vec_.getDim());
	}

}

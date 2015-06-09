// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

public class DenseArrayFloatFeatureVector implements FloatFeatureVector {

	private static final long serialVersionUID = 1L;
	private double[] weights_;
	
	public DenseArrayFloatFeatureVector(double[] weights) {
		weights_ = weights;
	}

	@Override
	public int getDim() {
		return weights_.length;
	}

	@Override
	public void updateFloatWeight(FloatWeights weights,
			int tag_index, int offset, double value) {
		int feature = offset;
		for (double weight : weights_) {
			int f_index = weights.getFloatIndex(feature, tag_index);
			weights.updateFloatWeight(f_index, weight * value);
			feature ++;
		}		
	}

	@Override
	public double getDotProduct(FloatWeights weights, int tag_index,
			int offset) {
		double score = 0.0;
		int feature = offset;
		for (double weight : weights_) {
				int f_index = weights.getFloatIndex(feature, tag_index);
				score += weight * weights.getFloatWeight(f_index);
				feature ++;
		}
		return score;
	}

	public double[] getValues() {
		return weights_;
	}
	
}

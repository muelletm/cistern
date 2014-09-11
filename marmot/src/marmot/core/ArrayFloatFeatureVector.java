// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

public class ArrayFloatFeatureVector implements FloatFeatureVector {

	private static final long serialVersionUID = 1L;
	private int[] features_;
	private double[] weights_;
	private int dim_;
	
	public ArrayFloatFeatureVector(int[] features,
			double[] weights, int dim) {
		features_ = features;
		weights_ = weights;
		dim_ = dim;
	}

	@Override
	public int getDim() {
		return dim_;
	}

	public void setDim(int dim) {
		dim_ = dim;
	}

	@Override
	public void updateFloatWeight(FloatWeights weights,
			int tag_index, int offset, double value) {
		
		for (int i=0; i<features_.length; i++) {
			int feature = features_[i];
			if (feature >= 0) {
				double weight = weights_[i];
				int f_index = weights.getFloatIndex(feature + offset, tag_index);
				weights.updateFloatWeight(f_index, weight * value);
			}
		}
		
	}

	@Override
	public double getDotProduct(FloatWeights weights, int tag_index,
			int offset) {
		double score = 0.0;
		for (int i=0; i<features_.length; i++) {
			int feature = features_[i];
			if (feature >= 0) {
				double weight = weights_[i];
				int f_index = weights.getFloatIndex(feature + offset, tag_index);
				score += weight * weights.getFloatWeight(f_index);				
			}
		}
		
		
		return score;
	}

	public double[] getWeights() {
		return weights_;
	}

	public int[] getFeatures() {
		return features_;
	}
	
}

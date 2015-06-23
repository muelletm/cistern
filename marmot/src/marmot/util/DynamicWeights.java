// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class DynamicWeights implements Serializable {

	private static final long serialVersionUID = 1L;
	private double[] weights_;
	private Random random_;
	private boolean clip_;
	private boolean expand_;

	public DynamicWeights(Random random) {
		weights_ = new double[0];
		random_ = random;
		clip_ = false;
		expand_ = true;
	}

	public DynamicWeights(double[] params, boolean clip, boolean expand) {
		weights_ = params;
		random_ = null;
		clip_ = clip;
		expand_ = expand;
	}

	public double[] getWeights() {
		return weights_;
	}

	public void setWeights(double[] weight) {
		weights_ = weight;
	}

	public double get(int index) {
		index = checkCapacity(index);
		return weights_[index];
	}

	private int checkCapacity(int index) {
		if (index >= weights_.length) {
			if (clip_) {
				index = index % weights_.length;
			} else if (expand_) {
				int old_length = weights_.length;
				int new_length = Math.max(index + 10, (weights_.length * 2) / 3);		
				expandArray(old_length, new_length);
			}
		}
		
		
		
		return index;
	}

	private void expandArray(int old_length, int new_length) {
		weights_ = Arrays.copyOf(weights_, new_length);
		if (random_ != null) {
			for (int i = old_length; i < weights_.length; i++) {
				weights_[i] = random_.nextGaussian();
			}
		}
	}

	public void increment(int index, double update) {
		index = checkCapacity(index);
		weights_[index] += update;
	}
	
	public void set(int index, double update) {
		index = checkCapacity(index);
		weights_[index] = update;
	}

	public int getLength() {
		return weights_.length;
	}

	public void setLength(int new_length) {
		expandArray(weights_.length, new_length);
	}

	public void setExapnd(boolean expand) {
		expand_ = false;
	}

}

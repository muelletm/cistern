// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.util.Arrays;

public class FeatureVector {
	@Override
	public String toString() {
		return "[features_=" + Arrays.toString(features_)
				+ ", length_=" + length_ + ", is_state_=" + is_state_
				+ ", concat_=" + concat_ + ", sub_vector_=" + sub_vector_
				+ ", float_vector_=" + float_vector_ + "]";
	}

	private int[] features_;

	private int length_;
	private boolean is_state_ = false;
	private boolean concat_;
	private FeatureVector sub_vector_;
	private FloatFeatureVector float_vector_;

	public FeatureVector(int capacity) {
		this(capacity, null, false);
	}

	public FeatureVector(int capacity, FeatureVector vector) {
		this(capacity, vector, true);
	}

	public FeatureVector(int capacity, FeatureVector vector, boolean concat) {
		features_ = new int[capacity];
		length_ = 0;
		is_state_ = false;
		sub_vector_ = vector;
		concat_ = concat;
	}

	public Integer get(int index) {
		if (index < length_ || !concat_)
			return features_[index];

		return sub_vector_.get(index - length_);
	}

	public boolean add(int feature) {
		if (feature >= 0) {
			
			if (length_ >= features_.length) {
				features_ = Arrays.copyOf(features_, features_.length * 2);
			}
			
			features_[length_++] = feature;
		}
		return true;
	}

	public int size() {
		if (concat_ && sub_vector_ != null) {
			return length_ + sub_vector_.size();
		}

		return length_;
	}

	public boolean getIsState() {
		return is_state_;
	}

	public void setIsState(boolean is_state) {
		is_state_ = is_state;
	}

	public void append(FeatureVector vector) {

		if (features_.length < vector.size() + length_) {
			int[] features = new int[vector.size() + length_];
			System.arraycopy(features_, 0, features, 0, length_);
			features_ = features;
		}

		for (int index = 0; index < vector.size(); index++) {
			add(vector.get(index));
		}
	}

	public int getDepth() {
		if (sub_vector_ == null)
			return 0;

		return sub_vector_.getDepth() + 1;
	}

	public void setConcat(boolean concat) {
		concat_ = concat;
	}

	public FloatFeatureVector getFloatVector() {
		if (float_vector_ != null) {
			assert sub_vector_ == null;
			return float_vector_;
		}
		
		if (sub_vector_ != null) {
			return sub_vector_.getFloatVector();
		}
		
		return null;
	}

	public void setFloatVector(FloatFeatureVector float_vector) {
		float_vector_ = float_vector;
	}

}

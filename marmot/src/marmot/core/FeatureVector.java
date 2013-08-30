// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.util.Iterator;

public class FeatureVector implements Iterable<Integer> {
	private int[] features_;
	private int length_;
	private boolean is_state_ = false;
	private boolean concat_;
	private FeatureVector sub_vector_;

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

	public FeatureVector getSubVector() {
		return sub_vector_;
	}

	public FeatureVector getDeepestVector() {
		if (sub_vector_ == null) {
			return this;
		}
		return sub_vector_.getDeepestVector();
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			int current = 0;

			@Override
			public boolean hasNext() {
				return current < size();
			}

			@Override
			public Integer next() {
				return get(current++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void append(FeatureVector vector) {

		if (features_.length < vector.size() + length_) {
			int[] features = new int[vector.size() + length_];
			System.arraycopy(features_, 0, features, 0, length_);
			features_ = features;
		}

		for (int feature : vector) {
			add(feature);
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

	public void setSubVector(FeatureVector sub_vector) {
		sub_vector_ = sub_vector;
	}
}

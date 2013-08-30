// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph;

import marmot.core.FeatureVector;

public class MorphFeatureVector extends FeatureVector {

	private int word_index_ = -1;

	public MorphFeatureVector(int capacity) {
		this(capacity, null, false);
	}

	public MorphFeatureVector(int capacity, FeatureVector vector) {
		this(capacity, vector, true);
	}
	
	public MorphFeatureVector(int capacity, FeatureVector vector, boolean concat) {
		super(capacity, vector, concat);
		word_index_ = -1;
	}
	
	public int getWordIndex() {
		return word_index_;
	}

	public void setWordIndex(int form_index) {
		word_index_ = form_index;
	}
	
}

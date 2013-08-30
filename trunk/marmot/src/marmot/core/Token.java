// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.io.Serializable;

public interface Token extends Serializable {
	public int[] getTagIndexes();
	public void setVector(FeatureVector vector);
	public FeatureVector getVector();
}

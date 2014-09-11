// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

public interface FloatWeights {

	public int getFloatIndex(int feature, int tag_index);
	public double getFloatWeight(int index);
	public void updateFloatWeight(int index, double value);
	
}

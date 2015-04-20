// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util.edit;

import java.io.Serializable;

import marmot.util.Counter;

public interface EditTree extends Serializable {

	public double getCost(EditTreeBuilder builder);
	
	public String apply(String input, int start, int end);
	
	public void increment(Counter<String> counter);

	public int getFixedInputLength();

	public int getFixedOutputLength();
		
}

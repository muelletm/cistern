// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

import java.util.List;

import marmot.core.State;
import marmot.core.WeightVector;



public interface SumLattice extends Lattice {
	double update(WeightVector weights_, double step_width);
	int getOrder();
	int getLevel();
	void setGoldCandidates(List<Integer> candidates);
	List<Integer> getGoldCandidates();
	List<List<State>> getZeroOrderCandidates(boolean filter);
}

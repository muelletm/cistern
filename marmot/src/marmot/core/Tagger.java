// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.io.Serializable;
import java.util.List;

import marmot.core.lattice.SumLattice;



public interface Tagger extends Serializable {
	List<List<String>> tag(Sequence sentence);
	Model getModel();
	int getNumLevels();
	SumLattice getSumLattice(boolean train, Sequence sequence);
	List<Integer> getGoldIndexes(Sequence sequence, List<List<State>> candidates);
	WeightVector getWeightVector();
	String setThresholds(boolean return_stats_as_string);
}

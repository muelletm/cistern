// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

import java.util.List;

import marmot.core.State;



public interface Lattice {
		public List<List<State>> getCandidates();
		public List<List<State>> prune();
}

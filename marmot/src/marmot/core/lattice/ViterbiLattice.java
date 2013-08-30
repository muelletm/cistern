// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

import java.util.List;

public interface ViterbiLattice extends Lattice {
		public Hypothesis getViterbiSequence();		
		public List<Hypothesis> getNbestSequences();
}

// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

public class LatticeEntry implements Comparable<LatticeEntry> {
	double score_;
	int previous_state_index_;
	
	public LatticeEntry(double score, int previous_state_index) {
		score_ = score;
		previous_state_index_ = previous_state_index;
	}

	public double getScore() {
		return score_;
	}

	public int getPreviousStateIndex() {
		return previous_state_index_;
	}

	@Override
	public int compareTo(LatticeEntry o) {
		return - Double.compare(score_, o.score_);
	}
	
	@Override
	public String toString() {
		return score_ + " " + previous_state_index_;
	}

}

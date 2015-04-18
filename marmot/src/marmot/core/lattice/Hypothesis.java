// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

import java.util.List;

import marmot.util.HashableIntArray;

public class Hypothesis implements Comparable<Hypothesis> {

	List<Integer> states_;
	double score_;
	HashableIntArray signature_;
	
	public Hypothesis(List<Integer> list, double score, HashableIntArray signature) {
		states_ = list;
		score_ = score;
		signature_ = signature;
	}

	@Override
	public int compareTo(Hypothesis o) {
		return - Double.compare(score_, o.score_);
	}
	
	@Override
	public String toString() {
		return score_ + " " + states_;
	}

	public HashableIntArray getSignature() {
		return signature_;
	}

	public List<Integer> getStates() {
		return states_;
	}

	public double getScore() {
		return score_;
	}
	
}

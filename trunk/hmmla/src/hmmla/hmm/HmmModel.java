// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.hmm;

public interface HmmModel {
	void getTransitions(int i, double[] scores);
	double getTransitions(int i, int j);
	void getEmissions(String word, double[] scores);
}

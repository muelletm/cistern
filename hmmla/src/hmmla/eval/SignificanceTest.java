// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.eval;

public interface SignificanceTest {

	public double test(Scorer scorer, String actual, String prediction1, String prediction2);
	
}

// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util.eval;

public interface SignificanceTest {

	public double test(Scorer scorer, String actual, String prediction1, String prediction2);
	
}

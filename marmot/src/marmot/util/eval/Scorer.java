// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util.eval;

import java.util.List;

public interface Scorer {

	public List<Double> getScores(String actual, String prediction);
	public void setOption(String option, String value);
	
}

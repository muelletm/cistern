// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.eval;

import java.util.List;

public interface Scorer {

	public List<Double> getScores(String actual, String prediction);
	public void setOption(String option, String value);
	
}

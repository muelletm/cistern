// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.Model;
import hmmla.io.Sentence;
import hmmla.util.Tuple;

import java.util.List;



public interface LossEstimator {
	public void estimateLosses(Model model, Iterable<Sentence> reader,
			List<Tuple<Integer, Double>> tuples);
}

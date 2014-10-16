// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.HmmModel;
import hmmla.hmm.HmmTrainer;
import hmmla.hmm.Model;
import hmmla.io.Sentence;

public abstract class EmTrainer {

	public double estep(HmmTrainer hmm_trainer, Model model, Iterable<Sentence> reader) {
		HmmModel normalizedStatistics = hmm_trainer.train(model);
		double ll = estep(model, normalizedStatistics, reader, true);
		return ll;
	}

	abstract public double estep(Model model, HmmModel hmm_model,
			Iterable<Sentence> reader, boolean update) ;
	
}

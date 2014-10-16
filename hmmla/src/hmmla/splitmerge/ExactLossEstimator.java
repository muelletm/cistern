// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.HmmModel;
import hmmla.hmm.HmmTrainer;
import hmmla.hmm.Model;
import hmmla.io.Sentence;
import hmmla.util.Copy;
import hmmla.util.SymbolTable;
import hmmla.util.Tuple;

import java.util.Collections;
import java.util.List;


public class ExactLossEstimator implements LossEstimator {
	protected EmTrainer trainer_;
	protected HmmTrainer hmm_trainer_;
	protected Merger merger_;

	public ExactLossEstimator(EmTrainer em_trainer, HmmTrainer hmm_trainer) {
		trainer_ = em_trainer;
		hmm_trainer_ = hmm_trainer;
		merger_ = new Merger(null);
	}

	@Override
	public void estimateLosses(Model model, Iterable<Sentence> reader,
			List<Tuple<Integer, Double>> tuples) {
		HmmModel hmm_model = hmm_trainer_.train(model);
		double ll = trainer_.estep(model, hmm_model, reader, false);

		SymbolTable<String> inputTable = model.getTagTable();
		int N = (inputTable.size() - 1) / 2;

		for (int index = 1; index < N + 1; index++) {
			Tuple<Integer, Double> tuple = new Tuple<Integer, Double>(index, 0.0);

			Model clone = (Model) Copy.clone(model);
			merger_.merge(clone, Collections.singletonList(tuple), 1);

			hmm_model = hmm_trainer_.train(clone);
			double new_ll = trainer_.estep(clone, hmm_model, reader, false);

			tuple.y = new_ll - ll;
			tuples.add(tuple);
		}
	}
}

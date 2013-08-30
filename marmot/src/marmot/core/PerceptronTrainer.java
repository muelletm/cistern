// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import marmot.core.lattice.SequenceViterbiLattice;
import marmot.core.lattice.SumLattice;
import marmot.core.lattice.ViterbiLattice;
import marmot.core.lattice.ZeroOrderSumLattice;
import marmot.core.lattice.ZeroOrderViterbiLattice;


public class PerceptronTrainer implements Trainer {

	private int steps_;
	private boolean shuffle_;
	private boolean verbose_;
	private boolean averaging_;

	@Override
	public void train(Tagger tagger, List<Sequence> sequences,
			Evaluator evaluator) {
		if (shuffle_) {
			sequences = new ArrayList<Sequence>(sequences);
		}

		int fraction = Math.max(sequences.size() / 4, 1);
		WeightVector weights = tagger.getWeightVector();
		assert weights != null;

		double[] sum_weights = null;
		if (averaging_) {
			sum_weights = new double[weights.getWeights().length];
		}
		
		Model model = tagger.getModel();

		for (int step = 0; step < steps_; step++) {
			if (verbose_)
				System.err.println("step: " + step);

			if (shuffle_)
				Collections.shuffle(sequences);

			int current_sentence = 0;
			long train_time = System.currentTimeMillis();
			for (Sequence sequence : sequences) {

				SumLattice sum_lattice = tagger.getSumLattice(true, sequence);

				List<List<State>> candidates = sum_lattice.getCandidates();

				ViterbiLattice lattice;
				if (sum_lattice instanceof ZeroOrderSumLattice) {
					lattice = new ZeroOrderViterbiLattice(candidates, 1);
				} else {
					lattice = new SequenceViterbiLattice(candidates,
							model.getBoundaryState(tagger.getNumLevels() - 1),
							1);
				}

				List<Integer> best_sequence = lattice.getViterbiSequence()
						.getStates();
				List<Integer> gold_sequence = sum_lattice.getGoldCandidates();

				
				
				if (!gold_sequence.equals(best_sequence)) {
					update(weights, candidates, gold_sequence, +1);
					update(weights, candidates, best_sequence, -1);
					
					if (averaging_) {
						double[] current_weights = weights.getWeights();
						int amount = sequences.size() - current_sentence;
						assert amount > 0;
						weights.setWeights(sum_weights);
						update(weights, candidates, gold_sequence, +amount);
						update(weights, candidates, best_sequence, -amount);
						weights.setWeights(current_weights);
					}
					
				}

				current_sentence++;				

				if (current_sentence % fraction == 0) {
					if (verbose_)
						System.err
								.format("Processed %d sentences at %g sentence/s \n",
										current_sentence,
										current_sentence
												/ ((System.currentTimeMillis() - train_time) / 1000.));

				}
			}

			if (averaging_) {
				double[] current_weights = weights.getWeights();
				for (int i = 0; i < current_weights.length; i++) {
					double scaling = (step + 1) * sequences.size();
					assert scaling > 0;
					current_weights[i] = sum_weights[i] / scaling;
					scaling = (step + 2) / (double) (step + 1);
					assert scaling > 0;
					assert scaling < 2 + 1e-5;
					sum_weights[i] *= scaling;
				}
			}

			if (evaluator != null && verbose_) {
				weights.setExtendFeatureSet(false);
				evaluator.eval(tagger);
				weights.setExtendFeatureSet(true);
			}
		}
		
		

		weights.setExtendFeatureSet(false);

	}

	private void update(WeightVector weights, List<List<State>> candidates,
			List<Integer> sequence, double amount) {

		int last_candidate_index = 0;
		for (int index = 0; index < sequence.size(); index++) {
			int candidate_index = sequence.get(index);

			State state = candidates.get(index).get(candidate_index);
			weights.updateWeights(state, amount, false);

			State transition = state.getTransition(last_candidate_index);
			weights.updateWeights(transition, amount, true);

			last_candidate_index = candidate_index;
		}
	}

	@Override
	public void setOptions(Options options) {
		steps_ = options.getNumIterations();
		shuffle_ = options.getShuffle();
		verbose_ = options.getVerbose();
		averaging_ = options.getAveraging();
	}

}

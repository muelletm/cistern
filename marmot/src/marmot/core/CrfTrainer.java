// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import marmot.core.lattice.SumLattice;

public class CrfTrainer implements Trainer {
	private double penalty_;
	private double step_width_ = .1;
	private double steps_;
	private boolean shuffle_;
	private boolean verbose_;
	private boolean very_verbose_;
	private double quadratic_penalty_;
	private long seed_;
	private boolean optimize_num_iterations_;

	@Override
	public void train(Tagger tagger, Collection<Sequence> in_sequences,
			Evaluator evaluator) {
		
		if (optimize_num_iterations_) {
			assert evaluator != null : "Set optimize_num_iterations but did not provide test data.";
		}
		
		Random rng = null;
		if (shuffle_) {
			if (seed_ == 0) {
				rng = new Random();
			} else {
				rng = new Random(seed_);
			}
		}
		
		List<Sequence> sequences = new ArrayList<Sequence>(in_sequences);

		int fraction = Math.max(sequences.size() / 4, 1);
		int smaller_fraction = Math.max(sequences.size() / 4000, 1);
		int small_factor = 1;
		WeightVector weights = tagger.getWeightVector();
		assert weights != null;
		
		double[] best_float_params = null;
		double[] best_params = null;
		double best_score = 0.0;

		double accumalted_penalty = 0;

		int number = 0;

		for (int step = 0; step < steps_; step++) {
			if (verbose_)
				System.err.println("step: " + step);

			if (shuffle_)
				Collections.shuffle(sequences, rng);

			int current_sentence = 0;
			long train_time = System.currentTimeMillis();
			for (Sequence sequence : sequences) {
				
				double step_width = step_width_
						/ (1 + (number / (double) sequences.size()));

				double scale_factor = 1 - 2. * step_width * quadratic_penalty_  / sequences.size(); 				
				assert !Double.isNaN(scale_factor);
				assert !Double.isInfinite(scale_factor);
				assert scale_factor > 1e-10;
				assert scale_factor < 1 + 1e-10;
				
				step_width /= scale_factor;
				
				if (Math.abs(penalty_) > 1e-10) {
					accumalted_penalty += step_width * penalty_
							/ sequences.size();
					weights.setPenalty(true, accumalted_penalty);
				}

				SumLattice lattice = tagger.getSumLattice(true, sequence);
				
				
				if (very_verbose_) {
					System.err.format("vv %d %d %d %d\n", number, lattice.getOrder() + lattice.getLevel() * (tagger.getModel().getOrder() + 1), lattice.getLevel(), lattice.getOrder() );
				}
				
				assert lattice != null;
				
				lattice.update(weights, step_width);		
				weights.scaleBy(scale_factor);
				current_sentence++;

				if (current_sentence % fraction == 0) {
					if (verbose_)
						System.err
								.format("Processed %d sentences at %g sentence/s \n",
										current_sentence,
										current_sentence
												/ ((System.currentTimeMillis() - train_time) / 1000.));

					if (small_factor < 100) {
						small_factor *= 10;
						smaller_fraction = Math.max(small_factor
								* sequences.size() / 400, 1);
					}
				}

				if (current_sentence % smaller_fraction == 0) {
					tagger.setThresholds(false);
				}
				
				number++;
			}

			if (evaluator != null && (verbose_ || optimize_num_iterations_)) {
				weights.setExtendFeatureSet(false);
				Result result = evaluator.eval(tagger);
				weights.setExtendFeatureSet(true);
				
				tagger.setResult(result);
				
				if (verbose_)
					System.err.println(result);
				
				if (optimize_num_iterations_) {
					
					double score = result.getScore();
					
					if (score > best_score) {
						best_score = score;
						best_params = weights.getWeights().clone();
						best_float_params = weights.getFloatWeights().clone();						
					}
					
				}
			}
		}

		weights.setPenalty(false, 0.0);
		weights.setExtendFeatureSet(false);
		
		if (optimize_num_iterations_) {
			if (best_params != null) {
				assert weights.getWeights().length == best_params.length;
				weights.setWeights(best_params);
			}
			
			if (best_float_params != null) {
				weights.setFloatWeights(best_float_params);
			}
			
			if (evaluator != null) {	
				Result result = evaluator.eval(tagger);
				tagger.setResult(result);
			}
		}
	}

	@Override
	public void setOptions(Options options) {
		setOptions(options.getPenalty(), options.getQuadraticPenalty(), options.getNumIterations(), options
				.getShuffle(), options.getVerbose(), options.getVeryVerbose(), options.getSeed(), options.getOptimizeNumIterations());	
	}

	private void setOptions(double penalty, double quadratic_penalty,
			int steps, boolean shuffle, boolean verbose,
			boolean very_verbose, long seed, boolean optimize_num_iterations) {
		penalty_ = penalty;
		steps_ = steps;
		shuffle_ = shuffle;
		verbose_ = verbose;
		very_verbose_ = very_verbose;
		quadratic_penalty_ = quadratic_penalty;
		seed_ = seed;
		optimize_num_iterations_ = optimize_num_iterations;
	}

}

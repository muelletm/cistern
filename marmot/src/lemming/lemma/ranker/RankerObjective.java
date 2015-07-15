// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.ranker;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lemming.lemma.LemmaCandidate;
import lemming.lemma.LemmaCandidateSet;
import lemming.lemma.ranker.RankerTrainer.RankerTrainerOptions;
import marmot.util.Numerics;
import cc.mallet.optimize.Optimizable.ByGradientValue;

public class RankerObjective implements ByGradientValue {

	private RankerModel model_;
	private List<RankerInstance> instances_;
	private double value_;
	private double[] gradient_;
	private double[] weights_;
	private double penalty_;
	private RankerTrainerOptions options_;
	private int max_num_duplicates_;

	public RankerObjective(RankerTrainerOptions options, RankerModel model,
			List<RankerInstance> instances, int max_num_duplicates) {
		options_ = options;
		model_ = model;
		instances_ = instances;
		weights_ = model.getWeights();
		gradient_ = new double[weights_.length];
		penalty_ = options.getQuadraticPenalty();
		max_num_duplicates_ = max_num_duplicates;
	}
	
	public RankerObjective(RankerTrainerOptions options, RankerModel model,
			List<RankerInstance> instances) {
		this(options, model, instances, 1);
	}

	
	public void update(RankerInstance instance, boolean sgd, double step_width) {
		if (!options_.getUseOfflineFeatureExtraction())
			model_.addIndexes(instance, instance.getCandidateSet(), true);

		int pos_index_ = instance.getPosIndex(model_.getPosTable(), false);
		int[] morph_indexes_ = instance.getMorphIndexes(model_.getMorphTable(), false);
		
		model_.setWeights(weights_);

		double logSum = Double.NEGATIVE_INFINITY;
		
		LemmaCandidateSet set = instance.getCandidateSet();
		for (Map.Entry<String, LemmaCandidate> entry : set) {
			LemmaCandidate candidate = entry.getValue();
			double score = model_.score(candidate, pos_index_, morph_indexes_);
			candidate.setScore(score);
			logSum = Numerics.sumLogProb(logSum, score);
		}

		double target_prob = Double.NEGATIVE_INFINITY;
		
		if (!sgd)
			model_.setWeights(gradient_);

		for (Map.Entry<String, LemmaCandidate> entry : set) {
			LemmaCandidate candidate = entry.getValue();
			double score = candidate.getScore();
			double prob = Math.exp(score - logSum);

			double update = -prob;

			String plemma = entry.getKey();
			if (plemma.equals(instance.getInstance().getLemma())) {
				update += 1.0;
				target_prob = prob;
				value_ += (score - logSum)
						* instance.getInstance().getCount();
			}
			
			if (sgd) {
				// For max_num_duplicates = 2, we created two copies of every instances with count >=2
				// Therefore we only count half the count here.
				
				double effective_count = instance.getInstance().getCount();
				if (Numerics.approximatelyGreaterEqual(effective_count, (double) max_num_duplicates_)) {
					effective_count /= (double) max_num_duplicates_;
				}
				
				// 1 -> 1, 2 -> 1.69, 3 -> 2.09 ...
				update *= Math.log(effective_count * Math.E);
			} else {
				update *= instance.getInstance().getCount();
			}

			model_.update(instance, plemma, update * step_width);
		}
		
		assert target_prob != Double.NEGATIVE_INFINITY;
		
		if (!options_.getUseOfflineFeatureExtraction())
			model_.removeIndexes(instance.getCandidateSet());
		
		model_.setWeights(weights_);
	}

	public void update() {
		// System.err.println("update");

		value_ = 0.;
		Arrays.fill(gradient_, 0.);

		for (RankerInstance instance : instances_) {
			update(instance, false, 1.0);
		}
		
		for (int i = 0; i < weights_.length; i++) {
			double w = weights_[i];
			value_ -= penalty_ * w * w;
			gradient_[i] -= 2. * penalty_ * w;
		}
	}

	@Override
	public int getNumParameters() {
		// System.err.println("getNumParameters");
		return weights_.length;
	}

	@Override
	public double getParameter(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getParameters(double[] params) {
		// System.err.println("getParameters");
		System.arraycopy(weights_, 0, params, 0, weights_.length);
	}

	@Override
	public void setParameter(int arg0, double arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParameters(double[] params) {
		// System.err.println("setParameters");
		System.arraycopy(params, 0, weights_, 0, weights_.length);
		update();
	}

	@Override
	public double getValue() {
		// System.err.println("getValue");
		return value_;
	}

	@Override
	public void getValueGradient(double[] gradient) {
		// System.err.println("getValueGradient " + gradient_.length + "  " +
		// gradient.length);
		System.arraycopy(gradient_, 0, gradient, 0, gradient_.length);
	}

}

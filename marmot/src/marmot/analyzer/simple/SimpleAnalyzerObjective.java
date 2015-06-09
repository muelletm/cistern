// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.analyzer.simple;

import java.util.Arrays;
import java.util.Collection;

import marmot.util.Numerics;

import cc.mallet.optimize.Optimizable.ByGradientValue;

public class SimpleAnalyzerObjective implements ByGradientValue {

	private SimpleAnalyzerModel model_;
	private Collection<SimpleAnalyzerInstance> instances_;
	private double value_;
	private double[] gradient_;
	private double[] weights_;
	private double penalty_;

	public SimpleAnalyzerObjective(double penalty, SimpleAnalyzerModel model,
			Collection<SimpleAnalyzerInstance> instances) {
		model_ = model;
		instances_ = instances;
		weights_ = model.getWeights();
		gradient_ = new double[weights_.length];
		penalty_ = penalty;
	}

	public void update() {
		// System.err.println("update");

		int num_tags = model_.getNumTags();
		
		value_ = 0.;
		Arrays.fill(gradient_, 0.);
		
		double[] scores = new double[num_tags];
		double[] updates = new double[num_tags];
		
		for (SimpleAnalyzerInstance instance : instances_) {
			model_.setWeights(weights_);
			model_.score(instance, scores);
			
			for (int tag_index=0; tag_index < num_tags; tag_index++) {
				double sum = Numerics.sumLogProb(scores[tag_index], 0);
				value_ -= sum;
				updates[tag_index] = - Math.exp(scores[tag_index] - sum);
			}
			
			for (int tag_index : instance.getTagIndexes()) {
				value_ += scores[tag_index];
				updates[tag_index] += 1.0;
			}
			
			model_.setWeights(gradient_);
			model_.update(instance, updates);
		}

		for (int i = 0; i < weights_.length; i++) {
			double w = weights_[i];
			value_ -= penalty_ * w * w;
			gradient_[i] -= 2. * penalty_ * w;
		}

		model_.setWeights(weights_);
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

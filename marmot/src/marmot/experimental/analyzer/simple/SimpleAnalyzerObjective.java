// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.experimental.analyzer.simple;

import java.util.Arrays;
import java.util.Collection;

import marmot.experimental.analyzer.simple.SimpleAnalyzer.Mode;
import marmot.util.Numerics;

import cc.mallet.optimize.Optimizable.ByGradientValue;

public class SimpleAnalyzerObjective implements ByGradientValue {

	private SimpleAnalyzerModel model_;
	private Collection<SimpleAnalyzerInstance> instances_;
	private double value_;
	private double[] gradient_;
	private double[] weights_;
	private double penalty_;
	private Mode mode_;

	public SimpleAnalyzerObjective(double penalty, SimpleAnalyzerModel model,
			Collection<SimpleAnalyzerInstance> instances, Mode mode) {
		model_ = model;
		instances_ = instances;
		weights_ = model.getWeights();
		gradient_ = new double[weights_.length];
		penalty_ = penalty;
		mode_ = mode;
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
			
			switch (mode_) {
			case binary:			
				value_ += binaryUpdate(scores, updates, num_tags, instance);
				break;
			case classifier:
				value_ += classifierUpdate(scores, updates, num_tags, instance);
				break;
			default:
				throw new RuntimeException("Unsupported mode: " + mode_);
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

	private double classifierUpdate(double[] scores, double[] updates,
			int num_tags, SimpleAnalyzerInstance instance) {
		double value = 0;
		double sum = Double.NEGATIVE_INFINITY;

		int num_tag_indexes = instance.getTagIndexes().size();
		
		for (int tag_index=0; tag_index < num_tags; tag_index++) {
			sum = Numerics.sumLogProb(scores[tag_index], sum);
		}
		
		value -= num_tag_indexes * sum;
		
		for (int tag_index=0; tag_index < num_tags; tag_index++) {
			updates[tag_index] = - num_tag_indexes * Math.exp(scores[tag_index] - sum);
		}
		
		for (int tag_index : instance.getTagIndexes()) {
			value += scores[tag_index];
			updates[tag_index] += 1.0;
		}
		
		return value;
	}

	private double binaryUpdate(double[] scores, double[] updates, int num_tags, SimpleAnalyzerInstance instance) {
		double value = 0;
		
		for (int tag_index=0; tag_index < num_tags; tag_index++) {
			double sum = Numerics.sumLogProb(scores[tag_index], 0);
			value -= sum;
			updates[tag_index] = - Math.exp(scores[tag_index] - sum);
		}
		
		for (int tag_index : instance.getTagIndexes()) {
			value += scores[tag_index];
			updates[tag_index] += 1.0;
		}
		return value;
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

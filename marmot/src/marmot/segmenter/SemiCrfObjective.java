package marmot.segmenter;

import java.util.Arrays;
import java.util.Collection;

import marmot.util.DynamicWeights;

import cc.mallet.optimize.Optimizable.ByGradientValue;

public class SemiCrfObjective implements ByGradientValue {

	private SegmenterModel model_;
	private Collection<Word> words_;
	private double value_;
	private double[] gradient_;
	private double[] weights_;
	private double penalty_;

	public SemiCrfObjective(SegmenterModel model, Collection<Word> words,
			double[] params) {
		model_ = model;
		words_ = words;
		weights_ = params;
		gradient_ = new double[params.length];

		model.setScorerWeights(new DynamicWeights(weights_, false, false));
		model.setUpdaterWeights(new DynamicWeights(gradient_, false, false));
	}

	public void update() {
		value_ = 0.;
		Arrays.fill(gradient_, 0.);

		SegmentationSumLattice lattice = new SegmentationSumLattice(model_);

		for (Word word : words_) {
			SegmentationInstance instance = model_.getInstance(word);
			value_ += lattice.update(instance, true);
		}

		System.err.println("value: " + value_);

		if (penalty_ > 0.0) {
			for (int i = 0; i < weights_.length; i++) {
				double w = weights_[i];
				value_ -= penalty_ * w * w;
				gradient_[i] -= 2. * penalty_ * w;
			}
		}
	}

	@Override
	public int getNumParameters() {
		return weights_.length;
	}

	@Override
	public double getParameter(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getParameters(double[] params) {
		System.arraycopy(weights_, 0, params, 0, weights_.length);
	}

	@Override
	public void setParameter(int arg0, double arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParameters(double[] params) {
		System.arraycopy(params, 0, weights_, 0, weights_.length);
		update();
	}

	@Override
	public double getValue() {
		return value_;
	}

	@Override
	public void getValueGradient(double[] gradient) {
		System.arraycopy(gradient_, 0, gradient, 0, gradient_.length);
	}

}

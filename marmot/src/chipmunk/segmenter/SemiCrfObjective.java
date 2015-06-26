package chipmunk.segmenter;

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
			double penalty) {
		model_ = model;
		words_ = words;
		penalty_ = penalty;
	}

	public void init() {
		DynamicWeights weights = new DynamicWeights(null);
		model_.setScorerWeights(weights);
		DynamicWeights gradient = new DynamicWeights(null);
		model_.setUpdaterWeights(gradient);
		model_.getUpdater().setInsert(false);

		calcLikelihood();

		DynamicWeights scorer = model_.getScorer().getWeights();
		DynamicWeights updater = model_.getUpdater().getWeights();

		if (scorer.getLength() != updater.getLength()) {
			int length = Math.max(scorer.getLength(), updater.getLength());
			scorer.setLength(length);
			updater.setLength(length);
		}

		weights_ = scorer.getWeights();
		scorer.setExapnd(false);
		gradient_ = updater.getWeights();
		updater.setExapnd(false);

		assert weights_.length == gradient_.length : weights_.length + " "
				+ gradient_.length;
		calcPenalty();
	}

	public void update() {
		value_ = 0.;
		Arrays.fill(gradient_, 0.);
		calcLikelihood();
		calcPenalty();
	}

	private void calcPenalty() {
		if (penalty_ > 0.0) {
			for (int i = 0; i < weights_.length; i++) {
				double w = weights_[i];
				value_ -= penalty_ * w * w;
				gradient_[i] -= 2. * penalty_ * w;
			}
		}
	}

	private void calcLikelihood() {
		SegmentationSumLattice lattice = new SegmentationSumLattice(model_);

		for (Word word : words_) {
			SegmentationInstance instance = model_.getInstance(word);
			value_ += lattice.update(instance, true);
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

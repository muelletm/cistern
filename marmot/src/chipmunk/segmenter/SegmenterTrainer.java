package chipmunk.segmenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;

import marmot.util.DynamicWeights;
import marmot.util.Numerics;

public class SegmenterTrainer {

	private SegmenterOptions options_;

	public SegmenterTrainer(SegmenterOptions options) {
		options_ = options;
	}

	public Segmenter train(Collection<Word> words) {
		SegmenterModel model = new SegmenterModel();

		model.init(options_, words);

		if (options_.getBoolean(SegmenterOptions.CRF_MODE)) {
			if (options_.getBoolean(SegmenterOptions.VERBOSE))
				System.err.println("Training CRF");
			run_crf(model, words);
		} else {
			if (options_.getBoolean(SegmenterOptions.VERBOSE))
				System.err.println("Training Perceptron");
			run_perceptron(model, words);
		}

		model.setFinal();

		Segmenter segmenter = new StatSegmenter(model);
		return segmenter;
	}

	private void run_crf(SegmenterModel model, Collection<Word> words) {
		SemiCrfObjective objective = new SemiCrfObjective(model, words, options_.getDouble(SegmenterOptions.PENALTY));
		objective.init();

		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);

		try {
			optimizer.optimize(1);

			for (int i = 0; i < 200 && !optimizer.isConverged(); i++) {
				optimizer.optimize(1);
			}

		} catch (IllegalArgumentException e) {
		} catch (OptimizationException e) {
		}
	}

	private void run_perceptron(SegmenterModel model, Collection<Word> words) {
		DynamicWeights weights = new DynamicWeights(null);
		DynamicWeights sum_weights = null;
		if (options_.getBoolean(SegmenterOptions.AVERAGING)) {
			sum_weights = new DynamicWeights(null);
		}

		model.setWeights(weights);

		SegmentationDecoder decoder = new SegmentationDecoder(model);

		int number;

		List<Word> word_array = new ArrayList<>(words);
		for (int iter = 0; iter < options_.getInt(SegmenterOptions.NUM_ITERATIONS); iter++) {

			number = 0;

			Collections.shuffle(word_array, options_.getRandom());
			for (Word word : word_array) {
				SegmentationInstance instance = model.getInstance(word);
				SegmentationResult result = decoder.decode(instance);

				double score = result.getScore();
				double exact_score = model.getScore(instance, result);
				assert Numerics.approximatelyEqual(score, exact_score) : String
						.format("%d %d", score, exact_score);

				if (!result.isCorrect(instance)) {

					SegmentationResult closest_result = Scorer.closest(result,
							instance.getResults(), instance.getLength());

					model.update(instance, result, -1.);
					model.update(instance, closest_result, +1.);

					if (sum_weights != null) { /* averaging */
						double amount = word_array.size() - number;
						assert amount > 0;
						model.setWeights(sum_weights);
						model.update(instance, result, -amount);
						model.update(instance, closest_result, +amount);
						model.setWeights(weights);
					}

				}

				number++;
			}

			if (sum_weights != null) { /* averaging */
				double weights_scaling = 1. / ((iter + 1.) * word_array.size());
				double sum_weights_scaling = (iter + 2.) / (iter + 1.);
				for (int i = 0; i < weights.getLength(); i++) {
					weights.set(i, sum_weights.get(i) * weights_scaling);
					sum_weights
							.set(i, sum_weights.get(i) * sum_weights_scaling);
				}
			}
		}
	}

}

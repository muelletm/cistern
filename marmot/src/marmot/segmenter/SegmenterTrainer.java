package marmot.segmenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import marmot.util.DynamicWeights;
import marmot.util.Numerics;

public class SegmenterTrainer {

	private int num_iterations_ = 15;
	private boolean averaging_ = false;
	private Random random_ = new Random(42);

	public Segmenter train(Collection<Word> words) {
		System.err.println("words.size: " + words.size());

		SegmenterModel model = new SegmenterModel();

		System.err.println("init");
		model.init(words);

		DynamicWeights weights = model.getWeights();
		DynamicWeights sum_weights = null;
		if (averaging_) {
			sum_weights = new DynamicWeights(null);
		}

		SegmentationDecoder decoder = new SegmentationDecoder(model);

		double correct;
		double total;
		double seg_correct;
		int number;

		Logger logger = Logger.getLogger(getClass().getName());

		List<Word> word_array = new ArrayList<>(words);
		for (int iter = 0; iter < num_iterations_; iter++) {

			logger.info(String.format("Iter: %3d / %3d", iter + 1,
					num_iterations_));

			correct = 0;
			seg_correct = 0;
			total = 0;
			number = 0;

			Collections.shuffle(word_array, random_);
			for (Word word : word_array) {
				SegmentationInstance instance = model.getInstance(word);
				SegmentationResult result = decoder.decode(instance);

				double score = result.getScore();
				double exact_score = model.getScore(instance, result);
				assert Numerics.approximatelyEqual(score, exact_score) : String
						.format("%d %d", score, exact_score);

				if (!result.isCorrect(instance)) {

					System.err.println("predict: "
							+ model.toWord(word.getWord(), result));
					System.err.println("correct: " + instance.getWord());

					if (result.isSegmentationCorrect(instance)) {
						seg_correct++;
					}

					model.update(instance, result, -1.);
					model.update(instance, instance.getFirstResult(), +1.);

					if (averaging_) {
						double amount = word_array.size() - number;
						assert amount > 0;
						model.setWeights(sum_weights);
						model.update(instance, result, -amount);
						model.update(instance, instance.getFirstResult(),
								+amount);
						model.setWeights(weights);
						weights = model.getWeights();
					}

				} else {
					correct++;
				}

				total++;
				number++;
			}

			if (averaging_) {
				double weights_scaling = 1. / ((iter + 1.) * word_array.size());
				double sum_weights_scaling = (iter + 2.) / (iter + 1.);
				for (int i = 0; i < weights.getLength(); i++) {
					weights.set(i, sum_weights.get(i) * weights_scaling);
					sum_weights
							.set(i, sum_weights.get(i) * sum_weights_scaling);
				}
			}

			logger.info(String.format(
					"Train Accuracy: %g / %g = %g (%g / %g = %g)", correct,
					total, correct * 100. / total, seg_correct + correct,
					total, (seg_correct + correct) * 100. / total));

		}

		System.err.println("ret");
		return null;
	}

}

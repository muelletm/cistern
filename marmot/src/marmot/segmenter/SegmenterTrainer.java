package marmot.segmenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;

import marmot.util.DynamicWeights;
import marmot.util.Numerics;

public class SegmenterTrainer {

	private int num_iterations_ = 15;
	private boolean averaging_ = true;
	private boolean use_crf = false;
	private double penalty_ = 0.0;
	private Random random_ = new Random(42);
	private int max_character_window_ = 3;
	private boolean use_segment_context_ = true;
	private boolean use_character_feature_ = true;
	private List<String> dictionary_paths_;
	private String lang_;
	
	public SegmenterTrainer(String lang) {
		lang_ = lang;
		dictionary_paths_ = new LinkedList<>();
	}

	public Segmenter train(Collection<Word> words) {
		SegmenterModel model = new SegmenterModel();

		model.init(lang_, words, max_character_window_, use_segment_context_, use_character_feature_, dictionary_paths_);

		if (use_crf)
			run_crf(model, words);
		else
			run_perceptron(model, words);

		model.setFinal();

		Segmenter segmenter = new Segmenter(model);
		return segmenter;
	}

	public SegmenterTrainer addDictionary(String path) {
		dictionary_paths_.add(path);
		return this;
	}	
	
	private void run_crf(SegmenterModel model, Collection<Word> words) {
		SemiCrfObjective objective = new SemiCrfObjective(model, words,
				penalty_);
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
		if (averaging_) {
			sum_weights = new DynamicWeights(null);
		}

		model.setWeights(weights);

		SegmentationDecoder decoder = new SegmentationDecoder(model);

		int number;

		List<Word> word_array = new ArrayList<>(words);
		for (int iter = 0; iter < num_iterations_; iter++) {

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
					}

				}

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
		}
	}

}

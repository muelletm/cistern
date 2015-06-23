package marmot.segmenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;
import cc.mallet.optimize.Optimizable.ByGradientValue;

import marmot.util.DynamicWeights;
import marmot.util.Numerics;
import marmot.util.Sys;

public class SegmenterTrainer {

	private int num_iterations_ = 15;
	private boolean averaging_ = false;
	private boolean use_crf = true;
	private Random random_ = new Random(42);

	public Segmenter train(Collection<Word> words) {
		SegmenterModel model = new SegmenterModel();

		Logger logger = Logger.getLogger(getClass().getName());
		
		logger.info("init");
		model.init(words);

		if (use_crf)
			run_crf(model, words);
		else
			run_perceptron(model, words);
		
		model.setFinal();
		
		Segmenter segmenter = new Segmenter(model);
		return segmenter;
	}

	private void run_crf(SegmenterModel model, Collection<Word> words) {
		Logger logger = Logger.getLogger(getClass().getName());
		logger.info("Start optimization");
		
		double[] params = new double[1000000];	
		
		ByGradientValue objective = new SemiCrfObjective(model, words, params);
		Optimizer optimizer = new LimitedMemoryBFGS(objective);
				
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);
		objective.setParameters(params);
		
        try {
        	optimizer.optimize(1);
        	
        	double memory_usage_during_optimization = Sys.getUsedMemoryInMegaBytes();
        	logger.info(String.format("Memory usage after first iteration: %g / %g MB", memory_usage_during_optimization, Sys.getMaxHeapSizeInMegaBytes()));

        	for (int i=0; i< 200 && !optimizer.isConverged(); i++) {
                optimizer.optimize(1);
                logger.info(String.format("Iteration: %3d / %3d", i + 1, 200));
        	}
        	

        } catch (IllegalArgumentException e) {
        } catch (OptimizationException e) {
        }
        
        logger.info("Finished optimization");
	}

	private void run_perceptron(SegmenterModel model, Collection<Word> words) {
		DynamicWeights weights = new DynamicWeights(null);
		DynamicWeights sum_weights = null;
		if (averaging_) {
			sum_weights = new DynamicWeights(null);
		}
		
		model.setWeights(weights);
		
		SegmentationDecoder decoder = new SegmentationDecoder(model);

		double correct;
		double total;
		double seg_correct;
		int number;

		Logger logger = Logger.getLogger(getClass().getName());

		List<Word> word_array = new ArrayList<>(words);
		for (int iter = 0; iter < num_iterations_; iter++) {

			double ll = 0;
			
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

//					System.err.println("predict: "
//							+ model.toWord(word.getWord(), result));
//					System.err.println("correct: " + instance.getWord());

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

			System.err.println("ll: " + ll);
			
			logger.info(String.format(
					"Train Accuracy: %g / %g = %g (%g / %g = %g)", correct,
					total, correct * 100. / total, seg_correct + correct,
					total, (seg_correct + correct) * 100. / total));

		}
		
	}

}

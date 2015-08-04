// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.ranker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import lemming.lemma.LemmaCandidateGenerator;
import lemming.lemma.LemmaCandidateGeneratorTrainer;
import lemming.lemma.LemmaInstance;
import lemming.lemma.LemmaOptions;
import lemming.lemma.LemmatizerGenerator;
import lemming.lemma.LemmatizerGeneratorTrainer;
import lemming.lemma.SimpleLemmatizerTrainer;
import lemming.lemma.edit.EditTreeGeneratorTrainer;
import lemming.lemma.edit.EditTreeGeneratorTrainer.EditTreeGeneratorTrainerOptions;
import lemming.lemma.ranker.RankerTrainer.RankerTrainerOptions;
import lemming.lemma.toutanova.EditTreeAligner;
import lemming.lemma.toutanova.EditTreeAlignerTrainer;
import marmot.util.Sys;
import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.Optimizable.ByGradientValue;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;

public class RankerTrainer implements LemmatizerGeneratorTrainer {

	public static class RankerTrainerOptions extends LemmaOptions {

		private static final long serialVersionUID = 1L;

		public static final String GENERATOR_TRAINERS = "generator-trainers";
		public static final String USE_PERCEPTRON = "use-perceptron";
		public static final String QUADRATIC_PENALTY = "quadratic-penalty";
		public static final String UNIGRAM_FILE = "unigram-file";
		public static final String USE_SHAPE_LEXICON = "use-shape-lexicon";
		public static final String ASPELL_LANG = "aspell-lang";
		public static final String ASPELL_PATH = "aspell-path";
		public static final String USE_CORE_FEATURES = "use-core-features";
		public static final String USE_ALIGNMENT_FEATURES = "use-alignment-features";
		public static final String IGNORE_FEATURES = "ignore-features";
		public static final String NUM_EDIT_TREE_STEPS = "num-edit-tree-steps";
		public static final String COPY_CONJUNCTONS = "copy-conjunctions";
		public static final String TAG_DEPENDENT = "tag-dependent";
		public static final String EDIT_TREE_MIN_COUNT = "edit-tree-min-count";
		public static final String EDIT_TREE_MAX_DEPTH = "edit-tree-max-depth";
		public static final String USE_HASH_FEATURE_TABLE = "use-hash-feature-table";
		public static final String USE_MALLET = "use-mallet";
		public static final String OFFLINE_FEATURE_EXTRACTION = "offline-feature-extraction";
		public static final String CLUSTER_FILE = "cluster-file";

		public RankerTrainerOptions() {
			map_.put(GENERATOR_TRAINERS, Arrays.asList(
					SimpleLemmatizerTrainer.class,
					EditTreeGeneratorTrainer.class));
			map_.put(USE_PERCEPTRON, true);
			map_.put(QUADRATIC_PENALTY, 0.00);
			map_.put(UNIGRAM_FILE, Arrays.asList(""));
			map_.put(USE_SHAPE_LEXICON, false);
			map_.put(ASPELL_LANG, "");
			map_.put(ASPELL_PATH, "");
			map_.put(USE_CORE_FEATURES, true);
			map_.put(USE_ALIGNMENT_FEATURES, true);
			map_.put(IGNORE_FEATURES, "");
			map_.put(NUM_EDIT_TREE_STEPS, 1);
			map_.put(COPY_CONJUNCTONS, false);
			map_.put(USE_HASH_FEATURE_TABLE, false);
			map_.put(TAG_DEPENDENT, false);
			map_.put(EDIT_TREE_MIN_COUNT, 0);
			map_.put(EDIT_TREE_MAX_DEPTH, -1);
			map_.put(USE_MALLET, true);
			map_.put(OFFLINE_FEATURE_EXTRACTION, true);
			map_.put(CLUSTER_FILE, "");
		}

		public RankerTrainerOptions(RankerTrainerOptions roptions) {
			map_ = new HashMap<>(roptions.map_);
		}

		@SuppressWarnings("unchecked")
		public List<Object> getUnigramFile() {
			return (List<Object>) getOption(UNIGRAM_FILE);
		}

		@SuppressWarnings("unchecked")
		public List<Object> getGeneratorTrainers() {
			return (List<Object>) getOption(GENERATOR_TRAINERS);
		}

		public boolean getUsePerceptron() {
			return (Boolean) getOption(USE_PERCEPTRON);
		}

		public double getQuadraticPenalty() {
			return (Double) getOption(QUADRATIC_PENALTY);
		}

		public List<LemmaCandidateGenerator> getGenerators(
				List<LemmaInstance> instances) {
			List<LemmaCandidateGenerator> generators = new LinkedList<>();
			for (Object trainer_class : getGeneratorTrainers()) {
				LemmaCandidateGeneratorTrainer trainer = (LemmaCandidateGeneratorTrainer) toInstance((Class<?>) trainer_class);
				if (trainer instanceof EditTreeGeneratorTrainer) {
					trainer.getOptions().setOption(
							EditTreeGeneratorTrainerOptions.NUM_STEPS,
							getNumEditTreeSteps());
					trainer.getOptions().setOption(
							EditTreeGeneratorTrainerOptions.TAG_DEPENDENT,
							getTagDependent());
					trainer.getOptions().setOption(
							EditTreeGeneratorTrainerOptions.MIN_COUNT,
							getEditTreeMinCount());
					trainer.getOptions().setOption(
							EditTreeGeneratorTrainerOptions.MAX_DEPTH,
							getEditTreeMaxDepth());
				}
				generators.add(trainer.train(instances, null));
			}
			return generators;
		}

		private Integer getEditTreeMaxDepth() {
			return (Integer) getOption(EDIT_TREE_MAX_DEPTH);
		}

		private Integer getEditTreeMinCount() {
			return (Integer) getOption(EDIT_TREE_MIN_COUNT);
		}

		public boolean getTagDependent() {
			return (Boolean) getOption(TAG_DEPENDENT);
		}

		public boolean getUseShapeLexicon() {
			return (Boolean) getOption(USE_SHAPE_LEXICON);
		}

		public String getAspellPath() {
			return (String) getOption(ASPELL_PATH);
		}

		public String getAspellLang() {
			return (String) getOption(ASPELL_LANG);
		}

		public boolean getUseCoreFeatures() {
			return (Boolean) getOption(USE_CORE_FEATURES);
		}

		public boolean getUseAlignmentFeatures() {
			return (Boolean) getOption(USE_ALIGNMENT_FEATURES);
		}

		public String getIgnoreFeatures() {
			return (String) getOption(IGNORE_FEATURES);
		}

		public int getNumEditTreeSteps() {
			return (Integer) getOption(NUM_EDIT_TREE_STEPS);
		}

		public boolean getCopyConjunctions() {
			return (Boolean) getOption(COPY_CONJUNCTONS);
		}

		public boolean getUseHashFeatureTable() {
			return (Boolean) getOption(USE_HASH_FEATURE_TABLE);
		}

		public boolean getUseMallet() {
			return (Boolean) getOption(USE_MALLET);
		}

		public boolean getUseOfflineFeatureExtraction() {
			return (Boolean) getOption(OFFLINE_FEATURE_EXTRACTION);
		}

		public String getClusterFile() {
			return (String) getOption(CLUSTER_FILE);
		}
	}

	private RankerTrainerOptions options_;
	private static final int MAX_NUM_DUPLICATES_ = 3;

	public RankerTrainer() {
		options_ = new RankerTrainerOptions();
	}

	@Override
	public LemmatizerGenerator train(List<LemmaInstance> train_instances,
			List<LemmaInstance> test_instances) {

		List<LemmaCandidateGenerator> generators = options_
				.getGenerators(train_instances);

		return trainReranker(generators, train_instances);
	}

	private LemmatizerGenerator trainReranker(
			List<LemmaCandidateGenerator> generators,
			List<LemmaInstance> simple_instances) {
		List<RankerInstance> instances = RankerInstance.getInstances(
				simple_instances, generators);

		RankerModel model = new RankerModel();

		EditTreeAligner aligner = (EditTreeAligner) new EditTreeAlignerTrainer(
				options_.getRandom(), false, 1, -1).train(simple_instances);

		Logger logger = Logger.getLogger(getClass().getName());
		logger.info("Extracting features");
		model.init(options_, instances, aligner);

		if (options_.getUsePerceptron()) {
			runPerceptron(model, instances);
		} else {
			runMaxEnt(model, instances);
		}

		return new Ranker(model, generators);
	}

	private void runMaxEnt(RankerModel model, List<RankerInstance> instances) {

		if (options_.getUseMallet()) {
			runMallet(model, instances);
		} else {
			runSgd(model, instances);
		}

	}

	private void runSgd(RankerModel model, List<RankerInstance> instances) {
		List<RankerInstance> duplicates = new LinkedList<>();
		for (RankerInstance instance : instances) {
			double count = instance.getInstance().getCount();
			int number = Math.min(MAX_NUM_DUPLICATES_, (int) count);
			for (int i = 0; i < number; i++) {
				duplicates.add(instance);
			}
		}

		Logger logger = Logger.getLogger(getClass().getName());
		logger.info(String.format(
				"Created duplicates. Increased num instances from %d to %d.\n",
				instances.size(), duplicates.size()));

		// instances = new LinkedList<>(instances);
		instances = duplicates;

		double initial_step_width = 0.1;

		RankerObjective objective = new RankerObjective(options_, model,
				instances, MAX_NUM_DUPLICATES_);

		Random random = options_.getRandom();	
		
		int number = 0;
		for (int step = 0; step < options_.getNumIterations(); step++) {
			logger.info("SGD step: " + step);

			Collections.shuffle(instances, random);
			for (RankerInstance instance : instances) {
				double step_width = initial_step_width
						/ (1 + (number / (double) instances.size()));
				objective.update(instance, true, step_width);
				number++;
			}
		}
	}

	private void runMallet(RankerModel model, List<RankerInstance> instances) {
		Logger logger = Logger.getLogger(getClass().getName());

		double memory_used_before_optimization = Sys.getUsedMemoryInMegaBytes();
		double memory_usage_of_one_weights_array = (double) model.getWeights().length
				* (double) Double.SIZE / (8. * 1024. * 1024.);
		logger.info(String.format("Memory usage of weights array: %g (%g) MB",
				Sys.getUsedMemoryInMegaBytes(model.getWeights(), false),
				memory_usage_of_one_weights_array));
		logger.info(String.format("Memory usage: %g / %g MB",
				memory_used_before_optimization,
				Sys.getMaxHeapSizeInMegaBytes()));

		logger.info("Start optimization");
		ByGradientValue objective = new RankerObjective(options_, model,
				instances);
		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		// Optimizer optimizer = new ConjugateGradient(objective);

		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);
		objective.setParameters(model.getWeights());

		try {
			optimizer.optimize(1);

			double memory_usage_during_optimization = Sys
					.getUsedMemoryInMegaBytes();
			logger.info(String.format(
					"Memory usage after first iteration: %g / %g MB",
					memory_usage_during_optimization,
					Sys.getMaxHeapSizeInMegaBytes()));

			for (int i = 0; i < 200 && !optimizer.isConverged(); i++) {
				optimizer.optimize(1);
				logger.info(String.format("Iteration: %3d / %3d", i + 1, 200));
			}

		} catch (IllegalArgumentException e) {
		} catch (OptimizationException e) {
		}

		logger.info("Finished optimization");
	}

	private void runPerceptron(RankerModel model, List<RankerInstance> instances) {
		Logger logger = Logger.getLogger(getClass().getName());

		double[] weights = model.getWeights();
		double[] sum_weights = null;
		if (options_.getAveraging()) {
			sum_weights = new double[weights.length];
		}

		for (int iter = 0; iter < options_.getNumIterations(); iter++) {

			double error = 0;
			double total = 0;
			int number = 0;

			Collections.shuffle(instances, options_.getRandom());
			for (RankerInstance instance : instances) {

				String lemma = model.select(instance);

				if (!lemma.equals(instance.getInstance().getLemma())) {

					model.update(instance, lemma, -1);
					model.update(instance, instance.getInstance().getLemma(),
							+1);

					if (sum_weights != null) {
						double amount = instances.size() - number;
						assert amount > 0;
						model.setWeights(sum_weights);
						model.update(instance, lemma, -amount);
						model.update(instance, instance.getInstance()
								.getLemma(), +amount);
						model.setWeights(weights);
					}

					error += instance.getInstance().getCount();

				}

				total += instance.getInstance().getCount();
				number++;
			}

			if (sum_weights != null) {

				double weights_scaling = 1. / ((iter + 1.) * instances.size());
				double sum_weights_scaling = (iter + 2.) / (iter + 1.);

				for (int i = 0; i < weights.length; i++) {
					weights[i] = sum_weights[i] * weights_scaling;
					sum_weights[i] = sum_weights[i] * sum_weights_scaling;
				}
			}

			logger.info(String.format("Train Accuracy: %g / %g = %g", total
					- error, total, (total - error) * 100. / total));

		}
	}

	@Override
	public LemmaOptions getOptions() {
		return options_;
	}

	public void setOptions(RankerTrainerOptions roptions) {
		options_ = roptions;
	}

}

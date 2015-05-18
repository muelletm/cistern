// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.ranker;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.lemma.LemmatizerGenerator;
import marmot.lemma.LemmatizerGeneratorTrainer;
import marmot.lemma.Options;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer.EditTreeGeneratorTrainerOptions;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.lemma.toutanova.EditTreeAlignerTrainer;
import marmot.util.Sys;
import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.Optimizable.ByGradientValue;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.optimize.Optimizer;


public class RankerTrainer implements LemmatizerGeneratorTrainer {



	public static class RankerTrainerOptions extends Options {
		
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

		

		public RankerTrainerOptions() {
			map_.put(GENERATOR_TRAINERS, Arrays.asList(SimpleLemmatizerTrainer.class, EditTreeGeneratorTrainer.class));
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
		
		public List<LemmaCandidateGenerator> getGenerators(List<Instance> instances) {
			List<LemmaCandidateGenerator> generators = new LinkedList<>();
			for (Object trainer_class  : getGeneratorTrainers()) {
				LemmaCandidateGeneratorTrainer trainer = (LemmaCandidateGeneratorTrainer) toInstance((Class<?>) trainer_class);
				if (trainer instanceof EditTreeGeneratorTrainer) {
					trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.NUM_STEPS, getNumEditTreeSteps());
					trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.TAG_DEPENDENT, getTagDependent());
					trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.MIN_COUNT, getEditTreeMinCount());
					trainer.getOptions().setOption(EditTreeGeneratorTrainerOptions.MAX_DEPTH, getEditTreeMaxDepth());
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
	} 
	
	private RankerTrainerOptions options_;
	
	public RankerTrainer() {
		options_ = new RankerTrainerOptions();
	}

	@Override
	public LemmatizerGenerator train(List<Instance> train_instances,
			List<Instance> test_instances) {

		List<LemmaCandidateGenerator> generators = options_.getGenerators(train_instances);

		return trainReranker(generators, train_instances);
	}

	private LemmatizerGenerator trainReranker(
			List<LemmaCandidateGenerator> generators,
			List<Instance> simple_instances) {
		List<RankerInstance> instances = RankerInstance.getInstances(simple_instances, generators);
				
		RankerModel model = new RankerModel();

		EditTreeAligner aligner = (EditTreeAligner) new EditTreeAlignerTrainer(options_.getRandom(), false, 1, -1)
				.train(simple_instances);

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
		Logger logger =Logger.getLogger(getClass().getName());
		
		double memory_used_before_optimization = Sys.getUsedMemoryInMegaBytes();
		double memory_usage_of_one_weights_array = (double) model.getWeights().length * (double) Double.SIZE / (8. * 1024. * 1024.);
		logger.info(String.format("Memory usage of weights array: %g (%g) MB", Sys.getUsedMemoryInMegaBytes(model.getWeights(), false), memory_usage_of_one_weights_array));
		logger.info(String.format("Memory usage: %g / %g MB", memory_used_before_optimization , Sys.getMaxHeapSizeInMegaBytes()));

		logger.info("Start optimization");
		ByGradientValue objective = new RankerObjective(options_, model, instances);
		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		//Optimizer optimizer = new ConjugateGradient(objective);
		
		
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);
		objective.setParameters(model.getWeights());
		
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
					model.update(instance, instance.getInstance().getLemma(), +1);

					if (sum_weights != null) {
						double amount = instances.size() - number;
						assert amount > 0;
						model.setWeights(sum_weights);
						model.update(instance, lemma, -amount);
						model.update(instance, instance.getInstance().getLemma(), +amount);						
						model.setWeights(weights);
					}
					
					error += instance.getInstance().getCount();
					
				} 
				
				total += instance.getInstance().getCount();
				number ++;
			}
			
			if (sum_weights != null) {

				double weights_scaling = 1. / ((iter + 1.) * instances
						.size());
				double sum_weights_scaling = (iter + 2.) / (iter + 1.);

				for (int i = 0; i < weights.length; i++) {
					weights[i] = sum_weights[i] * weights_scaling;
					sum_weights[i] = sum_weights[i] * sum_weights_scaling;
				}
			}
			
			logger.info(String.format("Train Accuracy: %g / %g = %g", total - error,
					total, (total - error) * 100. / total));
			
		}
	}

	@Override
	public Options getOptions() {
		return options_;
	}

}

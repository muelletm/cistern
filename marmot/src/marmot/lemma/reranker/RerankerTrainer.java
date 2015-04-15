package marmot.lemma.reranker;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.lemma.LemmaCandidateSet;
import marmot.lemma.LemmatizerGenerator;
import marmot.lemma.LemmatizerGeneratorTrainer;
import marmot.lemma.Options;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.edit.EditTreeGeneratorTrainer;
import marmot.lemma.toutanova.EditTreeAligner;
import marmot.lemma.toutanova.EditTreeAlignerTrainer;
import marmot.util.Runtime;
import cc.mallet.optimize.ConjugateGradient;
import cc.mallet.optimize.LimitedMemoryBFGS;
import cc.mallet.optimize.Optimizable.ByGradientValue;
import cc.mallet.optimize.Optimizer;


public class RerankerTrainer implements LemmatizerGeneratorTrainer {

	public static class RerankerTrainerOptions extends Options {
		
		public static final String GENERATOR_TRAINERS = "generator-trainers";
		public static final String USE_PERCEPTRON = "use-perceptron";
		public static String QUADRATIC_PENALTY = "quadratic-penalty";
		public static String UNIGRAM_FILE = "unigram-file";

		public RerankerTrainerOptions() {
			map_.put(GENERATOR_TRAINERS, Arrays.asList(SimpleLemmatizerTrainer.class, EditTreeGeneratorTrainer.class));
			map_.put(USE_PERCEPTRON, true);
			map_.put(QUADRATIC_PENALTY, 0.00);
			map_.put(UNIGRAM_FILE, "");
		}
		
		public String getUnigramFile() {
			String value = (String) getOption(UNIGRAM_FILE);
			if (value.isEmpty())
				return null;
			return value;
		}

		public List<Object> getGeneratorTrainers() {
			return (List<Object>) getOption(GENERATOR_TRAINERS);
		}

		public boolean getUsePerceptron() {
			return (Boolean) getOption(USE_PERCEPTRON);
		}

		public double getQuadraticPenalty() {
			return (Double) getOption(QUADRATIC_PENALTY);
		}
		
	} 
	
	private RerankerTrainerOptions options_;
	
	public RerankerTrainer() {
		options_ = new RerankerTrainerOptions();
	}

	@Override
	public LemmatizerGenerator train(List<Instance> train_instances,
			List<Instance> test_instances) {

		List<LemmaCandidateGenerator> generators = new LinkedList<>();
		for (Object trainer_class  : options_.getGeneratorTrainers()) {
			LemmaCandidateGeneratorTrainer trainer = (LemmaCandidateGeneratorTrainer) options_.toInstance((Class<?>) trainer_class);
			generators.add(trainer.train(train_instances, test_instances));
		}

		return trainReranker(generators, train_instances);
	}

	private LemmatizerGenerator trainReranker(
			List<LemmaCandidateGenerator> generators,
			List<Instance> simple_instances) {

		
		
		List<RerankerInstance> instances = new LinkedList<>();
		for (Instance instance : simple_instances) {

			LemmaCandidateSet set = new LemmaCandidateSet(instance.getForm());

			for (LemmaCandidateGenerator generator : generators) {
				generator.addCandidates(instance, set);
			}

			set.getCandidate(instance.getLemma());

			instances.add(new RerankerInstance(instance, set));
		}

		Model model = new Model();

		EditTreeAligner aligner = (EditTreeAligner) new EditTreeAlignerTrainer(options_.getRandom(), false)
				.train(simple_instances);

		Logger logger = Logger.getLogger(getClass().getName());
		logger.info("Extracting features");
		model.init(options_, instances, aligner);
			
		if (options_.getUsePerceptron()) {
			runPerceptron(model, instances);	
		} else {
			runMaxEnt(model, instances);
		}
		
		



		return new Reranker(model, generators);
	}

	private void runMaxEnt(Model model, List<RerankerInstance> instances) {
		Logger logger =Logger.getLogger(getClass().getName());
		
		double memory_used_before_optimization = Runtime.getUsedMemoryInMegaBytes();
		double memory_usage_of_one_weights_array = model.getWeights().length * Double.SIZE / (8. * 1024. * 1024.);
		logger.info(String.format("Memory usage of weights array: %g (%g) MB", Runtime.getUsedMemoryInMegaBytes(model.getWeights(), false), memory_usage_of_one_weights_array));
		logger.info(String.format("Memory usage: %g / %g MB", memory_used_before_optimization , Runtime.getMaxHeapSizeInMegaBytes()));

		logger.info("Start optimization");
		ByGradientValue objective = new RankerObjective(options_, model, instances);
		Optimizer optimizer = new LimitedMemoryBFGS(objective);
		//Optimizer optimizer = new ConjugateGradient(objective);
		
		
		Logger.getLogger(optimizer.getClass().getName()).setLevel(Level.OFF);
		objective.setParameters(model.getWeights());
		
        try {
        	optimizer.optimize(1);
        	
        	double memory_usage_during_optimization = Runtime.getUsedMemoryInMegaBytes();
        	logger.info(String.format("Memory usage: %g / %g MB", memory_usage_during_optimization, Runtime.getMaxHeapSizeInMegaBytes()));
        	logger.info(String.format("Additional weight arrays: %g", (memory_usage_during_optimization - memory_used_before_optimization) / memory_usage_of_one_weights_array));
        	
            optimizer.optimize(200);
        } catch (IllegalArgumentException e) {
        }
        
        logger.info("Finished optimization");
	}

	private void runPerceptron(Model model, List<RerankerInstance> instances) {
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
			for (RerankerInstance instance : instances) {

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

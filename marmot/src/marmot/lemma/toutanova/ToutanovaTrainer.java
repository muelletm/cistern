package marmot.lemma.toutanova;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.lemma.cmd.Trainer;
import marmot.morph.io.SentenceReader;

public class ToutanovaTrainer implements LemmatizerTrainer {

	public static class Options {
		
		private int num_iterations_;
		private boolean use_pos_;
		private long seed_;
		private int filter_alphabet_;
		private Aligner aligner_;
		private boolean averaging_;
		private int verbosity_;

		private Options() {
			num_iterations_ = 1;
			use_pos_ = false;
			seed_ = 42;
			filter_alphabet_ = 0;
			aligner_ = new SimpleAligner();
			averaging_ = false;
		}
		
		public Options setNumIterations(int iters) {
			num_iterations_ = iters;
			return this;
		}
		
		public static Options newInstance() {
			return new Options();
		}

		public int getNumIterations() {
			return num_iterations_;
		}

		public Options setUsePos(boolean b) {
			use_pos_ = b;
			return this;
		}

		public boolean getUsePos() {
			return use_pos_;
		}
		
		public long getSeed() {
			return seed_;
		}
		
		public Options setSeed(long s) {
			seed_ = s;
			return this;
		}

		public int getFilterAlphabet() {
			return filter_alphabet_;
		}

		public Options setFilterAlphabet(int i) {
			filter_alphabet_ = i;
			return this;
		}

		public Options setAligner(Aligner aligner) {
			aligner_ = aligner;
			return this;
		}

		public Aligner getAligner() {
			return aligner_;
		}
		
		public boolean getAveraging() {
			return averaging_;
		}

		public Options setAveraging(boolean b) {
			averaging_ = b;
			return this;
		}

		public Options setVerbosity(int i) {
			verbosity_ = i;
			return this;
		}

		public int getVerbosity() {
			return verbosity_;
		}
		
	}

	private Options options_;
	private Random random_;
	
	public ToutanovaTrainer(Options options) {
		options_ = options;
		random_ = new Random(options_.getSeed());
	}
	
	public static List<ToutanovaInstance> createToutanovaInstances(List<Instance> instances, Aligner aligner) {
		List<ToutanovaInstance> new_instances = new LinkedList<>();
		
		for (Instance instance : instances) {
			List<Integer> alignment = null;
			
			if (aligner != null) {
				alignment = aligner.align(instance.getForm(),
					instance.getLemma());
				assert alignment != null;
			}

			
			new_instances.add(new ToutanovaInstance(instance, alignment));
		}
		
		return new_instances;
	} 
	
	@Override
	public Lemmatizer train(List<Instance> train_instances,
			List<Instance> dev_instances) {

		Aligner aligner = options_.getAligner();

		List<ToutanovaInstance> new_train_instances = createToutanovaInstances(train_instances, aligner);

		List<ToutanovaInstance> new_dev_instances = null;
		if (dev_instances != null) {
			new_dev_instances = createToutanovaInstances(dev_instances, null);
		}

		return trainAligned(new_train_instances, new_dev_instances);
	}

	public Lemmatizer trainAligned(List<ToutanovaInstance> train_instances,
			List<ToutanovaInstance> dev_instances) {
		
		Logger logger = Logger.getLogger(getClass().getName());

		Model model = new Model();
		model.init(options_, train_instances, dev_instances);
		
		double[] weights = model.getWeights();
		double[] sum_weights = null;
		if (options_.getAveraging()) {
			sum_weights = new double[weights.length];
			Arrays.fill(sum_weights, 0.0);
		}
		
		Decoder decoder = new Decoder(model);

		double correct;
		double total;
		int number;

		List<ToutanovaInstance> token_instances = new LinkedList<>();
		for (ToutanovaInstance instance : train_instances) {
			if (!instance.isRare()) {
				for (int i=0; i<instance.getInstance().getCount(); i++) {
					token_instances.add(instance);
				}
			}
		}
		
		for (int iter = 0; iter < options_.getNumIterations(); iter++) {

			logger.info(String.format("Iter: %3d / %3d", iter + 1, options_.getNumIterations()));
			
			correct = 0;
			total = 0;
			number = 0;

			Collections.shuffle(token_instances, random_);
			for (ToutanovaInstance instance : token_instances) {

				Result result = decoder.decode(instance);
				String output = result.getOutput();

				if (!output.equals(instance.getInstance().getLemma())) {

					model.update(instance, result, -1.);
					model.update(instance, instance.getResult(), +1.);
					
					if (sum_weights != null) {
						double amount = token_instances.size() - number;
						assert amount > 0;
						model.setWeights(sum_weights);
						model.update(instance, result, -amount);
						model.update(instance, instance.getResult(), +amount);
						model.setWeights(weights);
					}
					
				} else {
					correct ++;
				}
				
				
				total ++;
				number ++;
				if (number % 1000 == 0 && options_.getVerbosity() > 0) {
					logger.info(String.format("Processed: %3d / %3d", number,
							token_instances.size()));
				}

			}
			
			if (sum_weights != null) {
				
				double weights_scaling = 1. / ((iter + 1.) * token_instances.size());
				double sum_weights_scaling = (iter + 2.) / (iter + 1.);
				
				for (int i = 0; i < weights.length; i++) {
					weights[i] = sum_weights[i] * weights_scaling;
					sum_weights[i] = sum_weights[i] * sum_weights_scaling;
				}
			}
			
			logger.info(String.format("Train Accuracy: %g / %g = %g", correct, total, correct * 100. / total));

		}

		return new ToutanovaLemmatizer(model);
	}
	
	public static void main(String[] args) {
		String trainfile = args[0];
		String testfile = args[1];
		
		SimpleLemmatizerTrainer.Options soptions = SimpleLemmatizerTrainer.Options.newInstance();
		soptions.setHandleUnseen(true).setHandleUnseen(true).setUseBackup(true).setUsePos(true);
		LemmatizerTrainer trainer = new SimpleLemmatizerTrainer(soptions);

		System.err.println("baseline");
		test(trainer, trainfile, testfile);
		
		Options options = Options.newInstance();
		options.setAligner(new SimpleAligner()).setAveraging(true).setFilterAlphabet(10).setNumIterations(10).setUsePos(true).setVerbosity(0);
		trainer = new ToutanovaTrainer(options);
		
		System.err.println("model");
		test(trainer, trainfile, testfile);
		
	}

	private static void test(LemmatizerTrainer trainer, String trainfile,
			String testfile) {
		List<Instance> training_instances = Trainer.getInstances(new SentenceReader(trainfile));
		
		Lemmatizer lemmatizer = trainer.train(training_instances, null);
		
		int correct = 0;
		int total = 0;
		
		List<Instance> test_instances = Trainer.getInstances(new SentenceReader(testfile));
		
		for (Instance instance : test_instances) {
			String predicted_lemma = lemmatizer.lemmatize(instance);
			
			if (predicted_lemma != null && predicted_lemma.equals(instance.getLemma())) {
				correct += instance.getCount();
			}
			total += instance.getCount();
		}
		
		double accuracy = correct * 100. / total;
		
		System.err.println(accuracy);
		
	}

}

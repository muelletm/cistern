package marmot.lemma.toutanova;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import marmot.lemma.BackupLemmatizer;
import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.SimpleLemmatizerTrainer;
import marmot.morph.io.SentenceReader;

public class ToutanovaTrainer implements LemmatizerTrainer {

	public static class Options implements Serializable {

		private int num_iterations_;
		private boolean use_pos_;
		private long seed_;
		private int filter_alphabet_;
		private AlignerTrainer aligner_trainer_;
		private boolean averaging_;
		private int verbosity_;
		private Class<?> decoder_class_;
		private boolean use_context_feature_;
		private int max_count_;

		private Options() {
			max_count_ = Integer.MAX_VALUE;
			num_iterations_ = 1;
			use_pos_ = false;
			seed_ = 42;
			filter_alphabet_ = 0;
			aligner_trainer_ = new SimpleAlignerTrainer();
			averaging_ = false;
			decoder_class_ = FirstOrderDecoder.class;
			use_context_feature_ = false;
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

		public Options setAlignerTrainer(AlignerTrainer trainer) {
			aligner_trainer_ = trainer;
			return this;
		}

		public AlignerTrainer getAligner() {
			return aligner_trainer_;
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

		public Decoder getDecoderInstance() {
			try {
				return (Decoder) decoder_class_.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		public Options setDecoder(Class<?> klass) {
			decoder_class_ = klass;
			return this;
		}

		public Class<?> getDecoderClass() {
			return decoder_class_;
		}

		public boolean getUseContextFeature() {
			return use_context_feature_;
		}

		public Options setUseContextFeature(boolean b) {
			use_context_feature_ = b;
			return this;
		}

		public String report() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("num_iterations: %s\n", num_iterations_));
			sb.append(String.format("use_pos: %s\n", use_pos_));
			sb.append(String.format("seed: %s\n", seed_));
			sb.append(String.format("filter alphabet: %s\n", filter_alphabet_));
			sb.append(String.format("aligner: %s\n", aligner_trainer_));
			sb.append(String.format("averaging: %s\n", averaging_));
			sb.append(String.format("decoder: %s\n", decoder_class_));
			sb.append(String.format("use context feature: %s\n",
					use_context_feature_));
			return sb.toString();
		}

		public Options setMaxCount(int d) {
			max_count_ = d;
			return this;
		}

		public int getMaxCount() {
			return max_count_;
		}

	}

	private Options options_;
	private Random random_;

	public ToutanovaTrainer(Options options) {
		options_ = options;
		random_ = new Random(options_.getSeed());
	}

	public ToutanovaTrainer() {
		this(Options.newInstance());
	}

	public static List<ToutanovaInstance> createToutanovaInstances(
			List<Instance> instances, Aligner aligner) {
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

		AlignerTrainer aligner_trainer = options_.getAligner();
		Aligner aligner = aligner_trainer.train(train_instances);

		List<ToutanovaInstance> new_train_instances = createToutanovaInstances(
				train_instances, aligner);

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

		Decoder decoder = (Decoder) options_.getDecoderInstance();
		decoder.init(model);

		double correct;
		double total;
		int number;

		List<ToutanovaInstance> token_instances = new LinkedList<>();
		for (ToutanovaInstance instance : train_instances) {
			if (!instance.isRare()) {
				for (int i = 0; i < Math.min(options_.getMaxCount(), instance
						.getInstance().getCount()); i++) {
					token_instances.add(instance);
				}
			}
		}

		for (int iter = 0; iter < options_.getNumIterations(); iter++) {

			logger.info(String.format("Iter: %3d / %3d", iter + 1,
					options_.getNumIterations()));

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
					correct++;
				}

				total++;
				number++;
				if (number % 1000 == 0 && options_.getVerbosity() > 0) {
					logger.info(String.format("Processed: %3d / %3d", number,
							token_instances.size()));
				}

			}

			if (sum_weights != null) {

				double weights_scaling = 1. / ((iter + 1.) * token_instances
						.size());
				double sum_weights_scaling = (iter + 2.) / (iter + 1.);

				for (int i = 0; i < weights.length; i++) {
					weights[i] = sum_weights[i] * weights_scaling;
					sum_weights[i] = sum_weights[i] * sum_weights_scaling;
				}
			}

			logger.info(String.format("Train Accuracy: %g / %g = %g", correct,
					total, correct * 100. / total));

		}

		return new ToutanovaLemmatizer(options_, model);
	}

	public static void main(String[] args) {
		String trainfile = args[0];
		String testfile = args[1];
		String mode = args[2];
		
		int tokens = 100000;
		
		SimpleLemmatizerTrainer.Options soptions = SimpleLemmatizerTrainer.Options.newInstance();
		soptions.setHandleUnseen(true).setHandleUnseen(true).setUseBackup(true).setUsePos(true);
		LemmatizerTrainer trainer = new SimpleLemmatizerTrainer(soptions);
		Lemmatizer baseline = train(trainer, trainfile, tokens);

		Options options = Options.newInstance();
		
		Logger logger = Logger.getLogger(ToutanovaTrainer.class.getName());
		
		for (String arg : mode.split(",")) {
			
			if (arg.equals("_")) {
				continue;
			} else if (arg.equalsIgnoreCase("hacky")) {
				options.setAlignerTrainer(new HackyAlignerTrainer());
			}
			else if (arg.equalsIgnoreCase("edit")) {
				options.setAlignerTrainer(new EditTreeAlignerTrainer(42));
			} else if (arg.equalsIgnoreCase("zero")) {
				options.setDecoder(ZeroOrderDecoder.class).setUseContextFeature(true);
			}
			else if (arg.equalsIgnoreCase("max_count")) {
					options.setMaxCount(1);
			} else {
				logger.warning(String.format("Unknown option: %s", arg));
			}
			
		}
		
		options.setAveraging(true).setFilterAlphabet(10).setNumIterations(10).setUsePos(true).setVerbosity(0);
		
		logger.info(options.report());
		
		trainer = new ToutanovaTrainer(options);
		Lemmatizer model = train(trainer, trainfile, tokens);
		
		
		soptions = SimpleLemmatizerTrainer.Options.newInstance();
		soptions.setHandleUnseen(false).setUseBackup(false).setUsePos(true).setAbstainIfAmbigous(true);
		LemmatizerTrainer simple_trainer = new SimpleLemmatizerTrainer(soptions);
		Lemmatizer simple_model = new BackupLemmatizer(train(simple_trainer, trainfile, tokens), model);

		logger.info("baseline");
		Lemmatizer.Result.logTest(baseline, testfile, 200);
		logger.info("model");
		Lemmatizer.Result.logTest(model, testfile, 200);
		logger.info("simple + model");
		Lemmatizer.Result.logTest(simple_model, testfile, 200);
		
	}

	private static Lemmatizer train(LemmatizerTrainer trainer,
			String trainfile, int tokens) {
		List<Instance> training_instances = Instance.getInstances(
				new SentenceReader(trainfile), tokens);
		return trainer.train(training_instances, null);
	}


}

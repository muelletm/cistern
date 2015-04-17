package marmot.lemma.toutanova;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.LemmatizerGenerator;
import marmot.lemma.LemmatizerGeneratorTrainer;
import marmot.lemma.Options;
import marmot.util.DynamicWeights;

public class ToutanovaTrainer implements LemmatizerGeneratorTrainer {

	public static class ToutanovaOptions extends Options {

		private static final long serialVersionUID = 1L;
		public static final String FILTER_ALPHABET = "filter-alphabet";
		public static final String ALIGNER_TRAINER = "aligner-trainer";
		public static final String DECODER = "decoder";
		public static final String USE_CONSTEXT_FEATURE = "use-context-feature";
		public static final String MAX_COUNT = "max-count";
		public static final String NBEST_RANK = "nbest-rank";
		
		public ToutanovaOptions() {
			super();
			
			map_.put(FILTER_ALPHABET, 5);
			map_.put(ALIGNER_TRAINER, EditTreeAlignerTrainer.class);
			map_.put(DECODER, ZeroOrderDecoder.class);
			map_.put(USE_CONSTEXT_FEATURE, true);
			map_.put(MAX_COUNT, 1);
			map_.put(NBEST_RANK, 50);
		}

		public static ToutanovaOptions newInstance() {
			return new ToutanovaOptions();
		}

		public int getFilterAlphabet() {
			return (Integer) getOption(FILTER_ALPHABET);
		}

		public AlignerTrainer getAligner() {
			return (AlignerTrainer) getInstance(ALIGNER_TRAINER);
		}

		public Decoder getDecoderInstance() {
			return (Decoder) getInstance(DECODER);
		}

		public boolean getUseContextFeature() {
			return (Boolean) getOption(USE_CONSTEXT_FEATURE);
		}

		public int getMaxCount() {
			return (Integer) getOption(MAX_COUNT);
		}

		public int getNbestRank() {
			return (Integer) getOption(NBEST_RANK);
		}

	}

	private ToutanovaOptions options_;

	public ToutanovaTrainer() {
		options_ = new ToutanovaOptions();
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
	public LemmatizerGenerator train(List<Instance> train_instances,
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

	public LemmatizerGenerator trainAligned(List<ToutanovaInstance> train_instances,
			List<ToutanovaInstance> dev_instances) {

		Logger logger = Logger.getLogger(getClass().getName());

		ToutanovaModel model = new ToutanovaModel();
		model.init(options_, train_instances, dev_instances);

		DynamicWeights weights = model.getWeights();
		DynamicWeights sum_weights = null;
		if (options_.getAveraging()) {
			sum_weights = new DynamicWeights(null);
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

			Collections.shuffle(token_instances, options_.getRandom());
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
						sum_weights = model.getWeights();
						model.update(instance, result, -amount);
						model.update(instance, instance.getResult(), +amount);
						model.setWeights(weights);
						weights = model.getWeights();
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

				for (int i = 0; i < weights.getLength(); i++) {
					weights.set(i, sum_weights.get(i) * weights_scaling);
					sum_weights.set(i, sum_weights.get(i) * sum_weights_scaling);
				}
			}

			logger.info(String.format("Train Accuracy: %g / %g = %g", correct,
					total, correct * 100. / total));

		}

		return new ToutanovaLemmatizer(options_, model);
	}

	@Override
	public Options getOptions() {
		return options_;
	}


}

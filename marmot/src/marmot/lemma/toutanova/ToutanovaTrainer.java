package marmot.lemma.toutanova;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;

public class ToutanovaTrainer implements LemmatizerTrainer {

	public static class Options {
		
		private int num_iterations_;
		private boolean use_pos_;
		private long seed_;
		private int filter_alphabet_;

		private Options() {
			num_iterations_ = 1;
			use_pos_ = false;
			seed_ = 42;
			filter_alphabet_ = 0;
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

		public int getFilterAlphabet() {
			return filter_alphabet_;
		}

		public void setFilterAlphabet(int i) {
			filter_alphabet_ = i;
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

		Aligner aligner = new SimpleAligner();

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

		Decoder decoder = new Decoder(model);

		int correct;
		int total;

		for (int iter = 0; iter < options_.getNumIterations(); iter++) {

			logger.info(String.format("Iter: %3d / %3d", iter, options_.getNumIterations()));
			
			correct = 0;
			total = 0;

			Collections.shuffle(train_instances, random_);
			for (ToutanovaInstance instance : train_instances) {

				if (instance.isRare())
					continue;
				
				Result result = decoder.decode(instance);
				String output = result.getOutput();

				if (!output.equals(instance.getInstance().getLemma())) {

					model.update(instance, result, -1);
					model.update(instance, instance.getResult(), +1);
					
				} else {
					correct++;
				}
				
				total++;
				if (total % 1000 == 0) {
					logger.info(String.format("Processed: %6d / %6d", total,
							train_instances.size()));
				}

			}
			
			logger.info(String.format("Train Accuracy: %d / %d = %g", correct, total,
					correct * 100. / total));

		}

		return new ToutanovaLemmatizer(model);
	}

}

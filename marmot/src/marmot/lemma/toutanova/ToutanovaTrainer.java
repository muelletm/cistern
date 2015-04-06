package marmot.lemma.toutanova;

import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.toutanova.ToutanovaTrainer.Options;

public class ToutanovaTrainer implements LemmatizerTrainer {

	public static class Options {
		
		private int num_iterations_;
		private boolean use_pos_;

		private Options() {
			num_iterations_ = 1;
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
		
	}

	private Options options_;
	
	public ToutanovaTrainer(Options options) {
		options_ = options;
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

		Model model = new Model();
		model.init(options_, train_instances, dev_instances);

		Decoder decoder = new Decoder(model);

		int number;
		int correct;
		int total;

		for (int epoch = 0; epoch < options_.getNumIterations(); epoch++) {

			correct = 0;
			total = 0;
			number = 0;

			for (ToutanovaInstance instance : train_instances) {
				number++;

				if (number % 100 == 0) {
					System.err.format("Processed %6d / %6d\n", number,
							train_instances.size());
				}

				Result result = decoder.decode(instance);
				String output = result.getOutput();

				if (!output.equals(instance.getInstance().getLemma())) {

					// System.err.println(output);
					model.update(instance, result, -1);
					model.update(instance, instance.getResult(), +1);
					// model.printWeights();
					
					
				} else {
					correct++;
				}
				total++;

			}

			System.err.format("Train Accuracy: %d / %d = %g\n", correct, total,
					correct * 100. / total);

		}

//		correct = 0;
//		total = 0;
//		number = 0;
//		for (ToutanovaInstance instance : dev_instances) {
//			number += 1;
//
//			Result result = decoder.decode(instance);
//
//			if (result.getOutput().equals(instance.getInstance().getLemma())) {
//				correct += 1;
//			}
//			total += 1;
//
//			if (number % 100 == 0) {
//				System.err.format("Accuracy: %d / %d = %g\n", correct, total,
//						correct * 100. / total);
//			}
//		}
//
//		System.err.format("Accuracy: %d / %d = %g\n", correct, total, correct
//				* 100. / total);

		return new ToutanovaLemmatizer(model);
	}

}

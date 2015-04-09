package marmot.util.edit;

import java.util.List;

import marmot.lemma.Instance;
import marmot.util.Counter;

public class EditTreeBuilderTrainer {

	long seed_;
	private int num_iterations_;

	public EditTreeBuilderTrainer(long seed) {
		this(seed, 1);
	}

	public EditTreeBuilderTrainer(long seed, int num_iterations) {
		seed_ = seed;
		num_iterations_ = num_iterations;
	}

	public EditTreeBuilder train(List<Instance> instances) {

		EditTreeBuilder builder = new EditTreeBuilder(seed_);

		for (int iter = 0; iter < num_iterations_; iter++) {
			Counter<String> counter = new Counter<>();
			for (Instance instance : instances) {
				EditTree tree = builder.build(instance.getForm(),
						instance.getLemma());
				tree.increment(counter);
			}
			builder.setCounter(counter);
		}

		return builder;
	}

}

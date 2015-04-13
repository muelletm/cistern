package marmot.util.edit;

import java.util.List;
import java.util.Random;

import marmot.lemma.Instance;
import marmot.util.Counter;

public class EditTreeBuilderTrainer {

	private Random random_;
	private int num_iterations_;

	public EditTreeBuilderTrainer(long seed) {
		this(new Random(seed), 1);
	}

	public EditTreeBuilderTrainer(long seed, int num_iterations) {
		this(new Random(seed), num_iterations);
	}
	
	public EditTreeBuilderTrainer(Random random, int num_iterations) {
		random_ = random;
		num_iterations_ = num_iterations;
	}

	public EditTreeBuilderTrainer(Random random) {
		this(random, 1);
	}

	public EditTreeBuilder train(List<Instance> instances) {

		EditTreeBuilder builder = new EditTreeBuilder(random_);

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

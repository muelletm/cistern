// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util.edit;

import java.util.List;
import java.util.Random;

import marmot.lemma.LemmaInstance;
import marmot.util.Counter;

public class EditTreeBuilderTrainer {

	private Random random_;
	private int num_iterations_;
	private int max_depth_;

	public EditTreeBuilderTrainer(Random random, int num_iterations, int max_depth) {
		random_ = random;
		num_iterations_ = num_iterations;
		max_depth_ = max_depth;
	}

	public EditTreeBuilder train(List<LemmaInstance> instances) {

		EditTreeBuilder builder = new EditTreeBuilder(random_, max_depth_);

		for (int iter = 0; iter < num_iterations_; iter++) {
			Counter<String> counter = new Counter<>();
			for (LemmaInstance instance : instances) {
				EditTree tree = builder.build(instance.getForm(),
						instance.getLemma());
				tree.increment(counter);
			}
			builder.setCounter(counter);
		}

		return builder;
	}

}

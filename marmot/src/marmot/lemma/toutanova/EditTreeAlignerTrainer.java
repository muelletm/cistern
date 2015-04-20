// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.toutanova;

import java.util.List;
import java.util.Random;

import marmot.lemma.Instance;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

public class EditTreeAlignerTrainer implements AlignerTrainer {

	private Random random_;
	private boolean merge_empty_input_segments_;

	public EditTreeAlignerTrainer() {
		this(new Random(42), true);
	}
	
	public EditTreeAlignerTrainer(Random random, boolean merge_empty_input_segments) {
		random_ = random;
		merge_empty_input_segments_ = merge_empty_input_segments;
	}

	@Override
	public Aligner train(List<Instance> instances) {
		EditTreeBuilder builder = new EditTreeBuilderTrainer(random_).train(instances);
		return new EditTreeAligner(builder, merge_empty_input_segments_);
	}
	
}

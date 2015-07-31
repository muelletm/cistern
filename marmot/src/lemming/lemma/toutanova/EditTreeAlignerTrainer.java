// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.toutanova;

import java.util.List;
import java.util.Random;

import lemming.lemma.LemmaInstance;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

public class EditTreeAlignerTrainer implements AlignerTrainer {

	private Random random_;
	private boolean merge_empty_input_segments_;
	private int num_steps_;
	private int max_depth_;

	public EditTreeAlignerTrainer(Random random, boolean merge_empty_input_segments, int num_steps, int max_depth) {
		random_ = random;
		merge_empty_input_segments_ = merge_empty_input_segments;
		num_steps_ = num_steps;
		max_depth_ = max_depth;
	}
	
	public EditTreeAlignerTrainer() {
		this(new Random(32), true, 1, -1);
	}

	@Override
	public Aligner train(List<LemmaInstance> instances) {
		EditTreeBuilder builder = new EditTreeBuilderTrainer(random_, num_steps_, max_depth_).train(instances);
		return new EditTreeAligner(builder, merge_empty_input_segments_);
	}
	
}

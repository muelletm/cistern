package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

public class EditTreeAlignerTrainer implements AlignerTrainer {

	private long seed_;

	public EditTreeAlignerTrainer(long seed) {
		seed_ = seed;
	}
	
	@Override
	public Aligner train(List<Instance> instances) {
		EditTreeBuilder builder = new EditTreeBuilderTrainer(seed_).train(instances);
		return new EditTreeAligner(builder);
	}

	
	
}

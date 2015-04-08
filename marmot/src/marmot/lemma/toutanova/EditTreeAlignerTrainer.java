package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

public class EditTreeAlignerTrainer implements AlignerTrainer {

	@Override
	public Aligner train(List<Instance> instances) {
		EditTreeBuilder builder = new EditTreeBuilderTrainer().train(instances);
		return new EditTreeAligner(builder);
	}

	
	
}

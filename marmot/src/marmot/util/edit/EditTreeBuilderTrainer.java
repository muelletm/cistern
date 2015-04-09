package marmot.util.edit;

import java.util.List;

import marmot.lemma.Instance;
import marmot.util.Counter;

public class EditTreeBuilderTrainer {
	
	long seed_;
	
	public EditTreeBuilderTrainer(long seed) {
		seed_ = seed;
	}

	public EditTreeBuilder train(List<Instance> instances) {
		
		Counter<String> counter = new Counter<>();
		
		EditTreeBuilder builder = new EditTreeBuilder(seed_);
		
		for (Instance instance : instances) {
			
			EditTree tree = builder.build(instance.getForm(), instance.getLemma());
			tree.increment(counter);
			
		}
		
		builder.setCounter(counter);
		return builder;
	}	
	
}

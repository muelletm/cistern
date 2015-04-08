package marmot.util.edit;

import java.util.List;

import marmot.lemma.Instance;
import marmot.util.Counter;

public class EditTreeBuilderTrainer {
	
	public EditTreeBuilderTrainer() {
	}

	public EditTreeBuilder train(List<Instance> instances) {
		
		Counter<String> counter = new Counter<>();
		
		EditTreeBuilder builder = new EditTreeBuilder();
		
		for (Instance instance : instances) {
			
			EditTree tree = builder.build(instance.getForm(), instance.getLemma());
			tree.increment(counter);
			
		}
		
		builder.setCounter(counter);
		return builder;
	}	
	
}

package marmot.lemma.edit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.util.Counter;
import marmot.util.Numerics;
import marmot.util.edit.EditTree;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

public class EditTreeGeneratorTrainer implements LemmaCandidateGeneratorTrainer {

	private double min_count_;
	
	public EditTreeGeneratorTrainer(int min_count) {
		min_count_ = min_count;
	}

	@Override
	public LemmaCandidateGenerator train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		EditTreeBuilder builder = new EditTreeBuilderTrainer(42).train(instances);
		
		Counter<EditTree> counter = new Counter<>();
		
		for (Instance instance : instances) {
			String form = instance.getForm();
			String lemma = instance.getLemma();
			EditTree tree = builder.build(form, lemma);
			counter.increment(tree, 1.0);
		}
		
		List<EditTree> trees = new LinkedList<>();
		
		for (Map.Entry<EditTree, Double> entry : counter.entrySet()) {
			double count = entry.getValue();
			
			if (Numerics.approximatelyGreaterEqual(count, min_count_)) {
				EditTree tree = entry.getKey();
				trees.add(tree);
			}
		}
		
		return new EditTreeGenerator(trees);
	}


}

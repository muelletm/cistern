package marmot.lemma.toutanova;

import java.util.LinkedList;
import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;

public class ToutanovaTrainer implements LemmatizerTrainer {

	
	
	@Override
	public Lemmatizer train(List<Instance> train_instances,
			List<Instance> dev_instances) {
		
		Model model = new Model();
		
		Aligner aligner = new SimpleAligner();
		
		List<ToutanovaInstance> new_train_instances = new LinkedList<>();
		for (Instance instance : train_instances) {
			List<Integer> alignment = aligner.align(instance.getForm(), instance.getLemma());
			new_train_instances.add(new ToutanovaInstance(instance, alignment));
		}

		List<ToutanovaInstance> new_dev_instances = new LinkedList<>();
		for (Instance instance : dev_instances) {
			new_train_instances.add(new ToutanovaInstance(instance, null));
		}
		
		
		model.init(new_train_instances, new_dev_instances);
		
		
		
		return null;
	}

}

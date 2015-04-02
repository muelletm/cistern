package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;

public class ToutanovaTrainer implements LemmatizerTrainer {

	@Override
	public Lemmatizer train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		System.out.println(instances.size());
		System.out.println(dev_instances.size());
		
		return null;
	}

	
	
}

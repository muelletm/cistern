package marmot.lemma;

import java.util.List;

public interface LemmatizerTrainer {

	public Lemmatizer train(List<Instance> instances, List<Instance> dev_instances);
	
	public Options getOptions();
	
	
	
}

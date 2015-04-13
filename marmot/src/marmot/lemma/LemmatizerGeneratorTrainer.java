package marmot.lemma;

import java.util.List;

public interface LemmatizerGeneratorTrainer  extends LemmatizerTrainer, LemmaCandidateGeneratorTrainer {

	public LemmatizerGenerator train(List<Instance> train_instances, List<Instance> test_instances);
	
}

package marmot.lemma;

import java.util.List;

public interface LemmaCandidateGeneratorTrainer {
	
	public LemmaCandidateGenerator train(List<Instance> instances, List<Instance> dev_instances);

}

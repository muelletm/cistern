package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;

public interface AlignerTrainer {

	public Aligner train(List<Instance> instances);
	
}

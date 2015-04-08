package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;

public class HackyAlignerTrainer implements AlignerTrainer {

	@Override
	public Aligner train(List<Instance> instances) {
		return new HackyAligner();
	}

}

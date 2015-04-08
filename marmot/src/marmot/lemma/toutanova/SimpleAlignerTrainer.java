package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;

public class SimpleAlignerTrainer implements AlignerTrainer {

	@Override
	public Aligner train(List<Instance> instances) {
		return new SimpleAligner();
	}

}

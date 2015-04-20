// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.util.List;

public interface LemmatizerGeneratorTrainer  extends LemmatizerTrainer, LemmaCandidateGeneratorTrainer {

	public LemmatizerGenerator train(List<Instance> train_instances, List<Instance> test_instances);
	
}

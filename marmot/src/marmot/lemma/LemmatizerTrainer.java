// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.util.List;

public interface LemmatizerTrainer {

	public Lemmatizer train(List<Instance> instances, List<Instance> dev_instances);
	
	public Options getOptions();
	
	
	
}

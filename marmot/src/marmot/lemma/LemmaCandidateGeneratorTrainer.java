// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.util.List;

public interface LemmaCandidateGeneratorTrainer {
	
	public LemmaCandidateGenerator train(List<LemmaInstance> instances, List<LemmaInstance> dev_instances);

	public LemmaOptions getOptions();
	
}

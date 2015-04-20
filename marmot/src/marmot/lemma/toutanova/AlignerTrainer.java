// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma.toutanova;

import java.util.List;

import marmot.lemma.Instance;

public interface AlignerTrainer {

	public Aligner train(List<Instance> instances);
	
}

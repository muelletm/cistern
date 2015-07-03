// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.toutanova;

import java.util.List;

import lemming.lemma.LemmaInstance;

public interface AlignerTrainer {

	public Aligner train(List<LemmaInstance> instances);
	
}

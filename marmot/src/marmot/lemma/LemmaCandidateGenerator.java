// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

import java.io.Serializable;


public interface LemmaCandidateGenerator extends Serializable {

	void addCandidates(Instance instance, LemmaCandidateSet set);  
	
}

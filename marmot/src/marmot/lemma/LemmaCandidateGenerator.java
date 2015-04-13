package marmot.lemma;

import java.io.Serializable;


public interface LemmaCandidateGenerator extends Serializable {

	void addCandidates(Instance instance, LemmaCandidateSet set);  
	
}

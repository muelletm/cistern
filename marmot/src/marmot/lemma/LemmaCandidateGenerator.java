package marmot.lemma;

import java.util.List;

public interface LemmaCandidateGenerator {

	List<String> getCandidates(Instance instance);  
	
}

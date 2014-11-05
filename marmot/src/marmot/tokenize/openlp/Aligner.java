package marmot.tokenize.openlp;

import java.util.List;

public interface Aligner {

	class Pair {
		int a;
		int b;
	}
	
	List<Pair> align(String a, String b);

}

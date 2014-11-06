package marmot.tokenize.openlp;

import java.util.List;

public interface Aligner {

	class Pair {
		public int a;
		public int b;
		
		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		@Override
		public String toString() {
			return String.format("(%d, %d)", a, b);
		}	
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Pair)) {
				return false;
			}
			
			Pair other_pair = (Pair) other;
			
			return a == other_pair.a && b == other_pair.b;
		}
				
	}
	
	List<Pair> align(String a, String b);
}

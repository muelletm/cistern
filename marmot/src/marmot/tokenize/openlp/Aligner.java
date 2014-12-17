// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.openlp;

import java.util.List;

public interface Aligner {

	public enum ResultType {
		Timeout,
		NoAlignmentFound,
		Standard
	}
	
	public class Result {
		public Result(ResultType result_type) {
			this(result_type, null);
		}
		public Result(ResultType result_type, List<Pair> pairs) {
			this.result_type = result_type;
			this.pairs = pairs;
		}
		public ResultType result_type;
		public List<Pair> pairs;
		
		@Override
		public String toString() {
			return String.format("(%s, %s)", result_type, pairs);
		}	
	}
	
	public class Pair {
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
	
	Result align(String a, String b);
}

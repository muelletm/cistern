// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.toutanova;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public interface Aligner extends Serializable {

	public static class StaticMethods {
		
		public static List<Integer> mergeEmptyInputSegments(List<Integer> alignment) {
			
			List<Integer> new_alignment = new LinkedList<>();
			
			Iterator<Integer> iter = alignment.iterator();
			
			int carry_output = 0;
			
			while (iter.hasNext()) {
				
				int input = iter.next();
				int output = iter.next();
				
				if (input == 0) {
					
					if (new_alignment.size() > 0) {
						new_alignment.set(new_alignment.size() -1, new_alignment.get(new_alignment.size() - 1) + output);
					} else {
						carry_output += output;
					}				
					
				} else {
					
					if (carry_output > 0) {
						output += carry_output;
						carry_output = 0;
					}
					
					new_alignment.add(input);
					new_alignment.add(output);
					
				}
				
			}
			
			return new_alignment;
		}
		
	} 
	
	List<Integer> align(String input, String output);

	public static class Pair {
		
		private String input_segment_;
		private String output_segment_;
		
		public Pair(String input_segment, String output_segment) {
			input_segment_ = input_segment;
			output_segment_ = output_segment;
		}

		public String getInputSegment() {
			return input_segment_;
		}
		
		public String getOutputSegment() {
			return output_segment_;
		}
		
		@Override 
		public String toString() {
			return String.format("%s %s", input_segment_, output_segment_);
		}
		
		@Override
		public boolean equals(Object other) {
			
			if (other == null) {
				return false;
			}
			
			if (!(other instanceof Pair)) {
				return false;
			}
			
			Pair other_pair = (Pair) other;
			
			return input_segment_.equals(other_pair.input_segment_) && output_segment_.equals(other_pair.output_segment_);
		}
	
		public static List<Pair> toPairs(String input, String output, List<Integer> indexes) {
		
			List<Pair> pairs = new LinkedList<>();
			
			int i_start_index = 0;
			int o_start_index = 0;
					
			Iterator<Integer> iterator = indexes.iterator();
			
			while (iterator.hasNext()) {
				
				int i_end_index = i_start_index + iterator.next();
				int o_end_index = o_start_index + iterator.next();
				
				String input_segment = input.substring(i_start_index, i_end_index);
				assert input_segment.length() > 0;
				String output_segment = output.substring(o_start_index, o_end_index);
				// assert output_segment.length() > 0;
				Pair pair = new Pair(input_segment, output_segment);
				
				pairs.add(pair);
				
				i_start_index = i_end_index;
				o_start_index = o_end_index;
				
			}
			
			
			
			return pairs;
		}	
	
	}
	
}

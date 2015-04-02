package marmot.lemma;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HackyAligner implements Aligner {

	private int max_segment_length_;
	private double[] cost_array_; 
	private int[] index_array_i_; 
	private int[] index_array_j_; 

	public HackyAligner() {
		max_segment_length_ = 5;
	}
	
	public HackyAligner(int max_segment_length) {
		max_segment_length_ = max_segment_length;
	}

	
	@Override
	public List<Integer> align(String input, String output) {
		checkArrayCapacity(input.length() * output.length());
		Arrays.fill(cost_array_, Double.POSITIVE_INFINITY);

		for (int i = 1; i < input.length() + 1; i++) {
			for (int j = 1; j < output.length() + 1; j++) {
				double best_cost = Double.POSITIVE_INFINITY;
				int best_start_i = -1;
				int best_start_j = -1;

				for (int i_start = Math.max(0, i - max_segment_length_); i_start < i; i_start++) {
					for (int j_start = Math.max(0, j - max_segment_length_); j_start < j; j_start++) {
					
						if ((i_start == 0 || j_start == 0)
								&& !(i_start == 0 && j_start == 0)) {
							continue;
						}

						double prev_cost = 0;
						if (i_start > 0 && j_start > 0) {
							prev_cost = cost_array_[(i_start - 1)
									* output.length() + j_start - 1];
						}

						double cost = getCost(input, i_start, i, output, j_start, j) + prev_cost;
						if (cost < best_cost) {
							best_cost = cost;
							best_start_i = i_start;
							best_start_j = j_start;
						}
					}
				}
				
				cost_array_[(i - 1) * output.length() + j - 1] = best_cost;
				index_array_i_[(i - 1) * output.length() + j - 1] = best_start_i;
				index_array_j_[(i - 1) * output.length() + j - 1] = best_start_j;
			}
		}

		List<Integer> list = new LinkedList<>();
		
		int end_i = input.length();
		int end_j = output.length();
		
		if (cost_array_[(end_i - 1) * output.length() + end_j - 1] == Double.POSITIVE_INFINITY) {
			throw new RuntimeException(String.format("Cannot align: %s - %s", input, output));
		}
		
		while (end_i > 0 && end_j > 0) {
			int start_i = index_array_i_[(end_i - 1) * output.length() + end_j - 1];
			int start_j = index_array_j_[(end_i - 1) * output.length() + end_j - 1];
			list.add(end_j - start_j);
			list.add(end_i - start_i);
			end_i = start_i;
			end_j = start_j;
		}
		
		Collections.reverse(list);
		return list;
	}

	private void checkArrayCapacity(int required_length) {
		if (cost_array_ == null || required_length > cost_array_.length) {
			cost_array_ = new double[required_length];
			index_array_i_ = new int[cost_array_.length];
			index_array_j_ = new int[cost_array_.length];
		}
	}

	private double getCost(String input, int i_start, int i, String output,
			int j_start, int j) {
		
		int i_length = i - i_start;
		int o_length = j - j_start;
		
		if (i_length == 1 && o_length == 1) {
			char c = input.charAt(i_start);
			
			if (c == output.charAt(j_start)) {
				
				if (c == '^' || c == '$') {
					return 1.0;
				}
				
				return 0;
			}
		}
		
		return i_length + o_length + 0.5;
		
	}

}

package marmot.lemma.toutanova;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Decoder {

	Model model_;

	private double[] score_array_;
	private int[] output_array_;
	private int[] index_array_;

	private int num_output_symbols_;
	private int input_length_;

	public Decoder(Model model) {
		model_ = model;
		num_output_symbols_ = model_.getOutputTable().size();
	}

	public List<Integer> decode(ToutanovaInstance instance) {
		int max_input_segment_length = model_.getMaxInputSegmentLength();
		input_length_ = instance.getFormCharIndexes().length;

		checkArraySize(num_output_symbols_ * input_length_);
		Arrays.fill(score_array_, Double.NEGATIVE_INFINITY);
		Arrays.fill(output_array_, -1);
		Arrays.fill(index_array_, -1);

		for (int l = 1; l < input_length_ + 1; l++) {
			for (int o = 0; o < num_output_symbols_; o++) {

				double best_score = Double.NEGATIVE_INFINITY;
				int best_output = -1;
				int best_index = -1;

				for (int l_start = Math.max(0, l - max_input_segment_length); l_start < l; l_start++) {

					double pair_score = model_.getPairScore(instance, l_start, l, o);
					
					if (l_start == 0) {

						double score = pair_score;

						if (score > best_score) {
							best_score = score;
							best_output = -1;
							best_index = l_start;
						}

					} else {

						for (int last_o = 0; last_o < num_output_symbols_; last_o++) {
							double prev_cost = score_array_[getIndex(o, l_start - 1)];
							double transiton_score = model_.getTransitionScore(instance, last_o, o,
									l_start, l);
							
							double score = pair_score + transiton_score + prev_cost;

							if (score > best_score) {
								best_score = score;
								best_output = last_o;
								best_index = l_start;
							}
						}
					}
				}

				score_array_[getIndex(o, l - 1)] = best_score;
				output_array_[getIndex(o, l - 1)] = best_output;
				index_array_[getIndex(o, l - 1)] = best_index;

			}
		}

		return backTrace();

	}

	private List<Integer> backTrace() {
		List<Integer> output = new LinkedList<>();
		int end_index = input_length_;
		double best_score = Double.NEGATIVE_INFINITY;
		int end_output = -1;

		for (int o = 0; o < num_output_symbols_; o++) {
			double score = score_array_[getIndex(o, end_index - 1)];

			if (score > best_score) {
				best_score = score;
				end_output = o;
			}
		}

		output.add(end_output);

		while (true) {

			int start_index = index_array_[getIndex(end_output, end_index - 1)];
			int start_output = output_array_[getIndex(end_output, end_index - 1)];
			
			if (start_output < 0)
				break;
			
			output.add(start_output);
			end_output = start_output;
			end_index = start_index;
		}

		Collections.reverse(output);
		return output;
	}

	private int getIndex(int output, int index) {
		return output * input_length_ + index;
	}

	private void checkArraySize(int required_length) {
		if (score_array_ == null || score_array_.length < required_length) {

			score_array_ = new double[required_length];
			output_array_ = new int[score_array_.length];
			index_array_ = new int[score_array_.length];

		}
	}

}

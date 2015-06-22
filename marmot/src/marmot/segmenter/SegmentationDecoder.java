package marmot.segmenter;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SegmentationDecoder {

	private SegmenterModel model_;
	private int num_tags_;
	private int max_segment_length;
	
	private double[] score_array_;
	private int[] tag_array_;
	private int[] index_array_;
	private int input_length_;
	private SegmentationInstance instance_;

	public SegmentationDecoder(SegmenterModel model) {
		model_ = model;
		num_tags_ = model_.getNumTags();
		max_segment_length = model_.getMaxSegmentLength();
	}
	
	SegmentationResult decode(SegmentationInstance instance) {
		instance_ = instance;
		input_length_ = instance.getLength();
		
		checkArraySize(num_tags_ * input_length_);
		Arrays.fill(score_array_, Double.NEGATIVE_INFINITY);
		Arrays.fill(tag_array_, -1);
		Arrays.fill(index_array_, -1);
		
		for (int l = 1; l < input_length_ + 1; l++) {
			for (int o = 0; o < num_tags_; o++) {

				double best_score = Double.NEGATIVE_INFINITY;
				int best_output = -1;
				int best_index = -1;

				for (int l_start = Math.max(0, l - max_segment_length); l_start < l; l_start++) {

					double pair_score = model_.getPairScore(instance, l_start, l, o);
					
					if (l_start == 0) {

						double score = pair_score;

						if (score > best_score) {
							best_score = score;
							best_output = -1;
							best_index = l_start;
						}

					} else {

						for (int last_o = 0; last_o < num_tags_; last_o++) {
							double prev_cost = score_array_[getIndex(last_o, l_start - 1)];
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
				tag_array_[getIndex(o, l - 1)] = best_output;
				index_array_[getIndex(o, l - 1)] = best_index;

			}
		}
		
		SegmentationResult result = backTrace();
		return result;
	}
	
	private SegmentationResult backTrace() {
		List<Integer> tags = new LinkedList<>();
		List<Integer> input_indexes = new LinkedList<>();
		int end_index = input_length_;
		double best_score = Double.NEGATIVE_INFINITY;
		int end_output = -1;

		for (int o = 0; o < num_tags_; o++) {
			double score = score_array_[getIndex(o, end_index - 1)];

			if (score > best_score) {
				best_score = score;
				end_output = o;
			}
		}

		tags.add(end_output);
		input_indexes.add(end_index);

		while (true) {

			int start_index = index_array_[getIndex(end_output, end_index - 1)];
			int start_output = tag_array_[getIndex(end_output, end_index - 1)];
			
			if (start_output < 0)
				break;
			
			tags.add(start_output);
			input_indexes.add(start_index);
			
			end_output = start_output;
			end_index = start_index;
		}

		Collections.reverse(tags);
		Collections.reverse(input_indexes);
		return new SegmentationResult(tags, input_indexes, best_score);
	}

	private int getIndex(int output, int index) {
		return output * input_length_ + index;
	}

	private void checkArraySize(int required_length) {
		if (score_array_ == null || score_array_.length < required_length) {
			score_array_ = new double[required_length];
			tag_array_ = new int[score_array_.length];
			index_array_ = new int[score_array_.length];
		}
	}

}

package chipmunk.segmenter;

import java.util.Arrays;

import marmot.util.Numerics;

public class SegmentationSumLattice {

	private SegmenterModel model_;
	private int num_tags_;
	private int max_segment_length;
	
	private double[] forward_score_array_;
	private double[] backward_score_array_;
	private int input_length_;

	public SegmentationSumLattice(SegmenterModel model) {
		model_ = model;
		num_tags_ = model_.getNumTags();
		max_segment_length = model_.getMaxSegmentLength();
	}
	
	public double update(SegmentationInstance instance, boolean do_update) {
		input_length_ = instance.getLength();
		
		checkArraySize(num_tags_ * input_length_);
		Arrays.fill(forward_score_array_, Double.NEGATIVE_INFINITY);
				
		for (int l_end = 1; l_end < input_length_ + 1; l_end++) {
			for (int tag = 0; tag < num_tags_; tag++) {
				double score_sum = Double.NEGATIVE_INFINITY;
				for (int l_start = Math.max(0, l_end - max_segment_length); l_start < l_end; l_start++) {
					double pair_score = model_.getPairScore(instance, l_start, l_end, tag);
					if (l_start == 0) {
						double score = pair_score;
						score_sum = Numerics.sumLogProb(score, score_sum);
					} else {
						for (int last_tag = 0; last_tag < num_tags_; last_tag++) {
							double prev_cost = forward_score_array_[getIndex(last_tag, l_start - 1)];
							double transiton_score = model_.getTransitionScore(instance, last_tag, tag,
									l_start, l_end);						
							double score = pair_score + transiton_score + prev_cost;
							score_sum = Numerics.sumLogProb(score, score_sum);
						}
					}
				}
				forward_score_array_[getIndex(tag, l_end - 1)] = score_sum;
				//System.err.println("FB scoresum tag" + tag + " " + score_sum);
			}
		}
		//double forward_sum = sumTag(forward_score_array_, input_length_ - 1);
		
		
		Arrays.fill(backward_score_array_, Double.NEGATIVE_INFINITY);
		for (int l_start = input_length_ - 1; l_start >= 0; l_start--) {
			for (int tag = 0; tag < num_tags_; tag++) {
				double score_sum = Double.NEGATIVE_INFINITY;
				for (int l_end = Math.min(input_length_, l_start + max_segment_length); l_end > l_start; l_end--) {
					double pair_score = model_.getPairScore(instance, l_start, l_end, tag);
					if (l_end == input_length_) {
						double score = pair_score;
						score_sum = Numerics.sumLogProb(score, score_sum);
					} else {
						for (int next_tag = 0; next_tag < num_tags_; next_tag++) {
							double prev_cost = backward_score_array_[getIndex(next_tag, l_end)];
							double transiton_score = model_.getTransitionScore(instance, tag, next_tag, l_start, l_end);						
							double score = pair_score + transiton_score + prev_cost;
							score_sum = Numerics.sumLogProb(score, score_sum);
						}
					}
				}
				backward_score_array_[getIndex(tag, l_start)] = score_sum;
			}
		}
		double backward_sum = sumTag(backward_score_array_, 0);
		double sum = backward_sum;
		
		for (int l_end = 1; l_end < input_length_ + 1; l_end++) {
			for (int tag = 0; tag < num_tags_; tag++) {
				for (int l_start = Math.max(0, l_end - max_segment_length); l_start < l_end; l_start++) {
					
					double pair_score = model_.getPairScore(instance, l_start, l_end, tag);
					
					double backward_score = 0.0;
					if (l_end < input_length_) {
						backward_score = Double.NEGATIVE_INFINITY;
						for (int next_tag = 0; next_tag < num_tags_; next_tag++) {
							double trans_score = model_.getTransitionScore(instance, tag, next_tag, l_start, l_end);
							double next_tag_score = backward_score_array_[getIndex(next_tag, l_end)] + trans_score;
							backward_score = Numerics.sumLogProb(next_tag_score, backward_score);
						}
					}
					
					if (l_start == 0) {
						double score = backward_score + pair_score;
						double log_prob = score - sum;
						double prob = Math.exp(log_prob);
						double update = -prob;
						
						if (do_update) {
							model_.update(instance, l_start, l_end, tag, update);
						}
					} else {
						
						double update = 0;
						
						for (int last_tag = 0; last_tag < num_tags_; last_tag++) {
							double forward_score = forward_score_array_[getIndex(last_tag, l_start - 1)];
							double transiton_score = model_.getTransitionScore(instance, last_tag, tag, l_start, l_end);
							double score = forward_score + pair_score + transiton_score + backward_score;
							double log_prob = score - sum;
							double prob = Math.exp(log_prob);
							double tag_update = -prob;
							
							if (do_update) {
								model_.update(instance, l_start, l_end, last_tag, tag, tag_update);
							}
							update += tag_update;
						}
						
						if (do_update) {
							model_.update(instance, l_start, l_end, tag, update);
						}
					}
				}
			}
		}
	
		double real_value = 0.0; 
		for (SegmentationResult result : instance.getResults()) {
				model_.update(instance, result, 1. / instance.getResults().size());
				real_value += model_.getScore(instance, result) - sum;
		}
		return real_value;
	}
	
	private double sumTag(double[] score_array, int l) {
		double score_sum = Double.NEGATIVE_INFINITY;
		for (int tag = 0; tag < num_tags_; tag++) {
			double score = score_array[getIndex(tag, l)];
			score_sum = Numerics.sumLogProb(score, score_sum);
		}
		return score_sum;
	}

	private int getIndex(int tag, int index) {
		return tag * input_length_ + index;
	}

	private void checkArraySize(int required_length) {
		if (forward_score_array_ == null || forward_score_array_.length < required_length) {
			forward_score_array_ = new double[required_length];
			backward_score_array_ = new double[required_length];
		}
	}

}

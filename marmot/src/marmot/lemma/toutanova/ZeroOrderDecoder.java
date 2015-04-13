package marmot.lemma.toutanova;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ZeroOrderDecoder implements Decoder {

	private Model model_;

	private double[] score_array_;
	private int[] output_array_;
	private int[] index_array_;

	private int num_output_symbols_;
	private int input_length_;
	private ToutanovaInstance instance_;

	public void init(Model model) {
		assert model != null;
		model_ = model;
		num_output_symbols_ = model_.getOutputTable().size();	
	}

	public Result decode(ToutanovaInstance instance) {

		int max_input_segment_length = model_.getMaxInputSegmentLength();
		input_length_ = instance.getFormCharIndexes().length;
		instance_ = instance;

		checkArraySize(input_length_);
		Arrays.fill(score_array_, Double.NEGATIVE_INFINITY);
		Arrays.fill(output_array_, -1);
		Arrays.fill(index_array_, -1);

		for (int l = 1; l < input_length_ + 1; l++) {
			
			double best_score = Double.NEGATIVE_INFINITY;
			int best_output = -1;
			int best_index = -1;
			
			for (int o = 0; o < num_output_symbols_; o++) {

				for (int l_start = Math.max(0, l - max_input_segment_length); l_start < l; l_start++) {

					double score = model_.getPairScore(instance, l_start, l, o);
					
					if (l_start > 0) {
						score += score_array_[(l_start - 1)];
					}
					
					if (score > best_score) {
						best_score = score;
						best_output = o;
						best_index = l_start;
					}
				}
			}

			score_array_[l - 1] = best_score;
			output_array_[l - 1] = best_output;
			index_array_[l - 1] = best_index;

		}
		
		Result result = backTrace();
		return result;
	}

	private Result backTrace() {
		List<Integer> outputs = new LinkedList<>();
		List<Integer> inputs = new LinkedList<>();
		
		int end_index = input_length_;

		while (true) {

			int start_index = index_array_[end_index - 1];
			int output = output_array_[end_index - 1];
			
			inputs.add(end_index);
			outputs.add(output);
			
			if (start_index == 0)
				break;
			
			end_index = start_index;
		}

		Collections.reverse(outputs);
		Collections.reverse(inputs);
		return new Result(model_, outputs, inputs, instance_.getInstance().getForm(), score_array_[input_length_ - 1]);
	}

	private void checkArraySize(int required_length) {
		if (score_array_ == null || score_array_.length < required_length) {

			score_array_ = new double[required_length];
			output_array_ = new int[score_array_.length];
			index_array_ = new int[score_array_.length];

		}
	}

}

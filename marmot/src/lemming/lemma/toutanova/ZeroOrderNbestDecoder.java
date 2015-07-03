// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.toutanova;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import marmot.util.HashableIntArray;
import marmot.util.Numerics;

public class ZeroOrderNbestDecoder implements NbestDecoder {

	private static class State implements Comparable<State> {
		private double score;
		private int output;
		private int index;

		@Override
		public int compareTo(State state) {
			return -Double.compare(score, state.score);
		}
	}

	private ToutanovaModel model_;
	private int num_output_symbols_;
	private int input_length_;
	private ToutanovaInstance instance_;

	private int rank_length_;

	private State[][] state_array_;
	private PriorityQueue<State> queue_;
	private Queue<Result> result_queue_;
	private Set<HashableIntArray> used_signatures_;

	public ZeroOrderNbestDecoder(int queue_size) {
		rank_length_ = queue_size;
		queue_ = new PriorityQueue<>();
		result_queue_ = new PriorityQueue<>();
		used_signatures_ = new HashSet<>();
	}
	
	@Override
	public void init(ToutanovaModel model) {
		model_ = model;
		num_output_symbols_ = model_.getOutputTable().size();
	}

	@Override
	public List<Result> decode(ToutanovaInstance instance) {
		assert model_ != null;
		assert num_output_symbols_ > 0;

		int max_input_segment_length = model_.getMaxInputSegmentLength();
		input_length_ = instance.getFormCharIndexes().length;
		instance_ = instance;

		checkArraySize(input_length_);

		for (int l = 1; l < input_length_ + 1; l++) {

			queue_.clear();

			for (int o = 0; o < num_output_symbols_; o++) {

				for (int l_start = Math.max(0, l - max_input_segment_length); l_start < l; l_start++) {

					double score = model_.getPairScore(instance, l_start, l, o);

					if (l_start > 0) {
						score += state_array_[(l_start - 1)][0].score;
					}

					State state = new State();
					state.score = score;
					state.output = o;
					state.index = l_start;

					queue_.add(state);
				}
			}

			for (int rank = 0; rank < rank_length_; rank++) {

				State state = queue_.poll();

				assert state == null || state.index < l;

				state_array_[l - 1][rank] = state;

			}
		}
		
		return backtrace();
	}

	private Result bySignature(HashableIntArray signature) {
		return bySignature(signature, false);
	}

	private Result bySignature(HashableIntArray signature, boolean debug) {

		List<Integer> outputs = new LinkedList<>();
		List<Integer> inputs = new LinkedList<>();

		int end_index = input_length_;

		double score = state_array_[input_length_ - 1][0].score;

		int signature_index = 0;
		int[] signature_array = signature.getArray();

		if (debug)
			System.err.println(score);

		while (true) {

			if (signature_index >= signature_array.length) {

				System.err.println(signature);
				System.err.println(instance_.getInstance().getForm());

			}

			int rank = signature_array[signature_index++];

			State state = state_array_[end_index - 1][rank];

			if (state == null) {
				return null;
			}

			int start_index = state.index;
			inputs.add(end_index);
			int output = state.output;
			outputs.add(output);

			// Calculate difference to best score and substract it from current score.
			
			double diff_to_best = state_array_[end_index - 1][0].score
					- state.score;
			assert diff_to_best >= 0.0;
			score = score - diff_to_best;

			if (debug)
				System.err.println(score + "  " + diff_to_best);

			if (start_index == 0)
				break;

			end_index = start_index;
		}

		// If not all positive rank values of a signature are used
		// then the signature is invalid as there is a second signature
		// that was produced earlier:
		// if the signature is 0 0 1 1, but the last 1 is not used
		// then 0 0 1 is identical.
		
		for (int i=signature_index; i<signature_array.length; i++) {
			if (signature_array[i] > 0) {
				return null;
			}
		}
		
		Collections.reverse(outputs);
		Collections.reverse(inputs);

		return new Result(model_, outputs, inputs, instance_.getInstance()
				.getForm(), score).setSignature(signature);
	}

	public List<Result> backtrace() {
		List<Result> list = new LinkedList<Result>();
		HashableIntArray signature = new HashableIntArray(
				new int[input_length_]);
		result_queue_.clear();
		used_signatures_.clear();
		result_queue_.add(bySignature(signature));
		used_signatures_.add(signature);

		while (list.size() < rank_length_) {
			Result result = result_queue_.poll();

			if (result == null) {
				break;
			}

			signature = result.getSignature();
			int[] signature_array = signature.getArray();

			result.setSignature(null);
			list.add(result);

			for (int index = 0; index < result.getOutputs().size(); index++) {

				int new_rank = signature_array[index] + 1;

				if (new_rank >= rank_length_)
					continue;

				int[] new_signature_array = Arrays.copyOf(signature_array,
						signature_array.length);
				new_signature_array[index] = new_rank;

				HashableIntArray new_signature = new HashableIntArray(
						new_signature_array);

				if (!used_signatures_.contains(new_signature)) {
					used_signatures_.add(new_signature);
					Result new_result = bySignature(new_signature);

					if (new_result != null) {

						if (!Numerics.approximatelyLesserEqual(
								new_result.getScore(), result.getScore())) {
							System.err.println(signature + " " + new_signature);

							bySignature(signature, true);
							bySignature(new_signature, true);

						}

						assert Numerics.approximatelyLesserEqual(
								new_result.getScore(), result.getScore());

						result_queue_.add(new_result);
					}
				}
			}
		}
		return list;
	}

	private void checkArraySize(int required_length) {
		if (state_array_ == null || state_array_.length < required_length) {
			state_array_ = new State[required_length][rank_length_];
		}
	}

}

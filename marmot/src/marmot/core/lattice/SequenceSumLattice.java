// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import marmot.core.State;
import marmot.core.Transition;
import marmot.core.WeightVector;
import marmot.util.Numerics;



public class SequenceSumLattice implements SumLattice {
	private ForwardSequenceLattice forward_;
	private BackwardSequenceLattice backward_;
	private List<List<State>> candidates_;
	private double log_threshold_;
	private boolean initilized_;
	private State boundary_;
	private int order_;
	private List<Integer> gold_candidate_indexes_;
	private boolean oracle_;
	
	public SequenceSumLattice(List<List<State>> candidates, State boundary,
			double threshold, int order, boolean oracle) {
		forward_ = new ForwardSequenceLattice(candidates, boundary);
		backward_ = new BackwardSequenceLattice(candidates, boundary);
		candidates_ = candidates;
		log_threshold_ = Math.log(threshold);
		initilized_ = false;
		boundary_ = boundary;
		order_ = order;
		oracle_ = oracle;
	}

	@Override
	public List<List<State>> getCandidates() {
		return candidates_;
	}

	public void init() {
		if (initilized_) {
			return;
		}
		initilized_ = true;

		forward_.init();
		backward_.init();
	}

	@Override
	public List<List<State>> prune() {
		return pruneStates();
	}
	
	public List<List<State>> pruneStates() {
		init();
		double score_sum_forward = forward_.partitionFunction();
		double score_sum_backward = backward_.partitionFunction();

		normalTest(score_sum_forward);
		normalTest(score_sum_backward);
		diffTest(score_sum_forward, score_sum_backward);

		List<List<State>> candidates = new ArrayList<List<State>>(
				candidates_.size());

		int[] index_map = null;
		int num_previous_states = 1;
		for (int index = 0; index < candidates_.size(); index++) {

			int num_states = candidates_.get(index).size();
			int[] new_index_map = new int[num_states];
			Arrays.fill(new_index_map, -1);
			double score_sum = Double.NEGATIVE_INFINITY;

			List<State> states = new ArrayList<State>(num_states);
			int max_state_index = -1;
			double max_score = Double.NEGATIVE_INFINITY;

			for (int state_index = 0; state_index < num_states; state_index++) {
				State state = candidates_.get(index).get(state_index);
				double score = forward_.get(index, state_index)
						+ backward_.get(index + 1, state_index);
				score_sum = Numerics.sumLogProb(score_sum, score);

				if (index_map != null) {
					boolean found_transition = false;
					for (int transition_index = 0; transition_index < num_previous_states; transition_index++) {
						State transition = state
								.getTransition(transition_index);
						if (transition != null
								&& index_map[transition_index] >= 0) {
							found_transition = true;
							break;
						}
					}
					if (!found_transition) {
						continue;
					}
				}
				
				boolean is_oracle_state = false;
				if (oracle_ && gold_candidate_indexes_ != null ) {
					is_oracle_state = gold_candidate_indexes_.get(index) == state_index;	
				}

				if ((score - score_sum_forward > log_threshold_) || is_oracle_state) {
					
					if (states.size() > 50)
						continue;
					
					states.add(fixTransitions(state, index_map,
							num_previous_states));
					new_index_map[state_index] = states.size() - 1;
				}

				if (score > max_score) {
					max_score = score;
					max_state_index = state_index;
				}

			}

			assert score_sum != Double.NEGATIVE_INFINITY;
			assert Math.abs(score_sum - score_sum_forward) < 1e-5;

			if (states.isEmpty()) {
				states.add(fixTransitions(
						candidates_.get(index).get(max_state_index), index_map,
						num_previous_states));
				new_index_map[max_state_index] = 0;
			}
			
			assert !states.isEmpty();
			candidates.add(states);
			num_previous_states = num_states;
			index_map = new_index_map;
		}

		assert candidates.size() == candidates_.size();	
		return candidates;
	}

	private State fixTransitions(State state, int[] index_map, int num_states) {
		if (index_map == null) {
			return state;
		}

		state = state.copy();

		Transition[] old_transitions = state.getTransitions();
		Transition[] new_transitions = new Transition[num_states];

		for (int index = 0; index < old_transitions.length; index++) {
			int new_index = index_map[index];
			if (new_index >= 0) {
				new_transitions[new_index] = old_transitions[index];
			}
		}

		state.setTransitions(new_transitions);
		return state;
	}

	@Override
	public double update(WeightVector weights, double step_width) {
		init();
		double ll = 0;

		double score_sum = forward_.partitionFunction();
		double score_sum_backward = backward_.partitionFunction();

		normalTest(score_sum);
		normalTest(score_sum_backward);

		diffTest(score_sum_backward, score_sum);
		
		int last_gold_candidate_index = 0;
		for (int index = 0; index < candidates_.size(); index++) {
			int gold_candidate_index = gold_candidate_indexes_.get(index);

			double state_sum = Double.NEGATIVE_INFINITY;
			double trans_sum = Double.NEGATIVE_INFINITY;
			double state_p_sum = 0;
			double trans_p_sum = 0;

			int state_index = 0;
			for (State state : candidates_.get(index)) {

				boolean is_gold_sequence_state = state_index == gold_candidate_index;

				int trans_index = 0;
				for (State transition : state.getTransitions()) {
					if (transition != null) {
						double trans_score = forward_.get(index - 1,
								trans_index)
								+ state.getScore()
								+ transition.getScore()
								+ backward_.get(index + 1, state_index);

						trans_sum = Numerics.sumLogProb(trans_sum, trans_score);

						double p = Math.exp(trans_score - score_sum);
						trans_p_sum += p;

						if (trans_index == last_gold_candidate_index && is_gold_sequence_state) {
							ll += transition.getScore();
							weights.updateWeights(transition, (1.0 - p) * step_width, true);
						} else {
							weights.updateWeights(transition, -p * step_width, true);
						}
					}
					trans_index++;
				}

				double state_score = forward_.get(index, state_index)
						+ backward_.get(index + 1, state_index);

				state_sum = Numerics.sumLogProb(state_sum, state_score);
				double p = Math.exp(state_score - score_sum);
				state_p_sum += p;

				if (is_gold_sequence_state) {
					ll += state.getScore();
					state.incrementEstimatedCounts((1.0 - p) * step_width);
				} else {
					state.incrementEstimatedCounts((- p) * step_width);
				}

				
				state_index++;
			}

			normalTest(state_sum);
			normalTest(trans_sum);
			diffTest(state_sum, score_sum);
			diffTest(trans_sum, score_sum);
			normalTest(state_p_sum);
			normalTest(trans_p_sum);
			diffTest(state_p_sum, 1.0);
			diffTest(trans_p_sum, 1.0);
			
			for (State state : candidates_.get(index)) {
				state.updateWeights(weights);
			}	
			
			last_gold_candidate_index = gold_candidate_index;
		}
		
		ll -= score_sum;
		return ll;
	}

	protected void normalTest(double score) {
		if ((Double.isNaN(score) || Double.isInfinite(score))) {
			throw new RuntimeException("normalTest: " + score);
		}
	}

	protected double diffTest(double a, double b) {
		double diff = Math.abs(a - b);
		if (diff > 1.e-5) {
			throw new RuntimeException(String.format("test failed: %g %g : %g",
					a, b, diff));
		}
		return diff;
	}

	@Override
	public int getOrder() {
		return order_;
	}

	public static List<List<State>> getZeroOrderCandidates(List<List<State>> candidates, int boundary_index) {
		List<List<State>> new_candidates = new ArrayList<List<State>>(candidates.size());
		boolean found_boundary = false;
		for (List<State> states : candidates) {
			List<State> new_states = new ArrayList<State>();
			

			for (State state : states) {
				State zero_order_state = state.getZeroOrderState();
				assert !(zero_order_state instanceof Transition);
				
				if (zero_order_state.getIndex() == boundary_index) {
					found_boundary = true;
				}
				
				boolean contains = false;
				for (State new_state : new_states) {
					
					if (new_state.equalIndexes(zero_order_state)) {
						contains = true;
						break;
					}
					
				}
				
				if (!contains) {
					State new_state = zero_order_state.copy();
					new_state.setTransitions(null);
					new_states.add(new_state);
					assert new_state.getIndex() >= 0;
					assert new_state.getTransitions() == null;
				}
				
			}
						
			new_candidates.add(new_states);
			if (found_boundary) {
				assert new_states.size() == 1;
				break;
			}
			
			assert !new_states.isEmpty();
		}
		assert !new_candidates.isEmpty();
		
		for (List<State> states: new_candidates) {
			for (State state : states) {
				assert state.getTransitions() == null;
			}
		}
		
		assert found_boundary;
		return new_candidates;

	}
	
	@Override
	public List<List<State>> getZeroOrderCandidates(boolean filter) {
		
		List<List<State>> candidates;
		if (filter) {
			candidates = prune();
		} else {
			candidates = candidates_;
		}
		
		List<List<State>> new_candidates = new ArrayList<List<State>>(candidates.size());
		boolean found_boundary = false;
		for (List<State> states : candidates) {
			List<State> new_states = new ArrayList<State>();
			

			for (State state : states) {
				State zero_order_state = state.getZeroOrderState();
				assert !(zero_order_state instanceof Transition);
				
				if (zero_order_state.getIndex() == boundary_.getIndex()) {
					found_boundary = true;
				}
				
				boolean contains = false;
				for (State new_state : new_states) {
					
					if (new_state.equalIndexes(zero_order_state)) {
						contains = true;
						break;
					}
					
				}
				
				if (!contains) {
					State new_state = zero_order_state.copy();
					new_state.setTransitions(null);
					new_states.add(new_state);
					assert new_state.getIndex() >= 0;
					assert new_state.getTransitions() == null;
				}
				
			}
						
			new_candidates.add(new_states);
			if (found_boundary) {
				assert new_states.size() == 1;
				break;
			}
			
			assert !new_states.isEmpty();
		}
		assert !new_candidates.isEmpty();
		
		for (List<State> states: new_candidates) {
			for (State state : states) {
				assert state.getTransitions() == null;
			}
		}
		
		assert found_boundary;
		return new_candidates;
	}

	@Override
	public void setGoldCandidates(List<Integer> candidates) {
		gold_candidate_indexes_ = candidates;
	}

	@Override
	public int getLevel() {
		return candidates_.get(0).get(0).getLevel();
	}

	@Override
	public List<Integer> getGoldCandidates() {
		return gold_candidate_indexes_;
	}

}

// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import marmot.core.State;
import marmot.core.WeightVector;
import marmot.lemma.ranker.RankerCandidate;
import marmot.util.Check;
import marmot.util.Numerics;



public class ZeroOrderSumLattice implements SumLattice {
	private List<List<State>> candidates_;
	private double log_threshold_;
	private List<Integer> gold_candidate_indexes_;
	private double[] score_sums_;
	private boolean initialized_;
	private boolean oracle_;
	
	public ZeroOrderSumLattice(List<List<State>> candidates, double threshold, boolean oracle) {
		candidates_ = candidates;
		log_threshold_ = Math.log(threshold);
		initialized_ = false;
		oracle_ = oracle;
	}

	private void init() {
		if (initialized_)
			return;	
		initialized_ = true;
		score_sums_ = new double[candidates_.size()];
		
		for (int index = 0; index < candidates_.size(); index++) {
			List<State> states = candidates_.get(index); 
			assert !states.isEmpty();
			score_sums_[index] = getScoreSum(states);
		}
	}

	@Override
	public List<List<State>> getCandidates() {
		return candidates_;
	}

	@Override
	public List<List<State>> prune() {
		init();
		List<List<State>> candidates = new ArrayList<List<State>>(
				candidates_.size());

		for (int index = 0; index < candidates_.size(); index++) {
			int num_states = candidates_.get(index).size();
			assert num_states >= 0;
			
			double score_sum = score_sums_[index];

			List<State> states = new ArrayList<State>(num_states);

			State max_state = null;
			double max_score = Double.NEGATIVE_INFINITY;

			int state_index = 0;
			for (State state : candidates_.get(index)) {
				double score = state.getScore() - score_sum;
				assert Check.isNormal(score);
				
				boolean is_oracle_state = false;
				if (oracle_ && gold_candidate_indexes_ != null ) {
					is_oracle_state = gold_candidate_indexes_.get(index) == state_index;	
				}
				
				if (score > log_threshold_ || is_oracle_state) {
					states.add(state);
				}

				if (score > max_score) {
					max_score = score;
					max_state = state;
				}

				state_index ++;
			}

			assert max_state != null;
			
			if (states.isEmpty()) {
				states.add(max_state);
			}

			candidates.add(states);
		}

		return candidates;
	}
	
	private double getScoreSum(Collection<State> states) {
		double score_sum = Double.NEGATIVE_INFINITY;
		for (State state : states) {
			assert Check.isNormal(state.getScore());
			score_sum = Numerics.sumLogProb(score_sum, state.getScore());
		}
		assert score_sum != Double.NEGATIVE_INFINITY;
		assert Check.isNormal(score_sum);
		return score_sum;
	}
	
	// Faster but numerically less robust.
//	private double getScoreSum(Collection<State> states) {
//		double score_sum = 0.0;
//		for (State state : states) {
//			score_sum += Math.exp(state.getScore());
//		}
//		return Math.log(score_sum);
//	}

	@Override
	public double update(WeightVector weights, double step_width) {
		init();
		double ll = 0;
		
		if (gold_candidate_indexes_ == null) {
			System.err.println("Warning: Gold sequence not in zero order lattice!");
			return ll;
		}

		for (int index = 0; index < candidates_.size() - 1; index++) {
			int gold_candidate_index = gold_candidate_indexes_.get(index);
			List<State> states = candidates_.get(index);		
			double score_sum = score_sums_[index];
			ll += update(states, gold_candidate_index, score_sum, weights, step_width);
		}

		return ll;
	}

	private double update(List<State> states, int gold_candidate_index,
			double score_sum, WeightVector weights, double step_width) {
		int candidate_index = 0;
		double ll=0;
		
		for (State state : states) {
			assert state.getZeroOrderState() == state;

			double p = Math.exp(state.getScore() - score_sum);

			double value = -p;
			
			if (candidate_index == gold_candidate_index) {
				value += 1.0;
				ll = states.get(gold_candidate_index).getScore() - score_sum;
			}
			
			
			
			weights.updateWeights(state, value * step_width, false);
			
			if (state.getLemmaCandidates() != null) {
				double new_score = state.getRealScore();
				for (RankerCandidate candidate : state.getLemmaCandidates()) {
					double score = candidate.getScore() + new_score; 
					p = Math.exp(score  - score_sum);
					value = -p;
					if (candidate.isCorrect() && candidate_index == gold_candidate_index) {
						value += 1.0;
						ll = score - score_sum;
					}
					candidate.update(state, weights, value * step_width);
				}
			}
			
			candidate_index++;
		}
		
		return ll;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public List<List<State>> getZeroOrderCandidates(boolean filter) {
		if (filter) {
			return prune();
		}
		return candidates_;
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

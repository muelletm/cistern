// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;


import java.util.Collections;
import java.util.List;

import marmot.core.State;
import marmot.util.Numerics;


public class ForwardSequenceLattice {
	private double[][] lattice_;
	private List<List<State>> candidates_;
	private State boundary_;

	public ForwardSequenceLattice(List<List<State>> candidates, State boundary) {
		candidates_ = candidates;
		boundary_ = boundary;
	}

	public void init() {
		lattice_ = new double[candidates_.size()][];
		
		List<State> previous_states = Collections.singletonList(boundary_);
		for (int index = 0; index < candidates_.size(); index++) {
			List<State> states = candidates_.get(index);
			lattice_[index] = new double[states.size()];
			int state_index = 0;
			for (State state : states) {
				
				double score_sum = Double.NEGATIVE_INFINITY;
			
				for (int previous_state_index = 0; previous_state_index < previous_states
						.size(); previous_state_index++) {

					State transition = state.getTransition(previous_state_index);
					
					if (transition == null) {
						continue;
					}
					
					double score = state.getScore() + transition.getScore();
					
					if (index > 0) {
						score += lattice_[index - 1][previous_state_index];
					}

					score_sum = Numerics.sumLogProb(score_sum, score);
				}
				
				lattice_[index][state_index] = score_sum;
				state_index ++;
			}
			previous_states = states;
		}
		
		assert lattice_[candidates_.size() - 1].length == 1;
	}

	double partitionFunction() {
		return lattice_[candidates_.size() - 1][0];
	}

	public double get(int index, int state_index) {
		if (index == -1) {
			return 0;
		}
		
		return lattice_[index][state_index];
	}
}

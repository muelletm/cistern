// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;


import java.util.Collections;
import java.util.List;

import marmot.core.State;
import marmot.util.Numerics;


public class BackwardSequenceLattice {
	private double[][] lattice_;
	private List<List<State>> candidates_;
	private State boundary_;

	public BackwardSequenceLattice(List<List<State>> candidates, State boundary) {
		candidates_ = candidates;
		boundary_ = boundary;

		assert candidates_.get(candidates_.size() - 1).size() == 1;
		State state = candidates_.get(candidates_.size() - 1).get(0);
		assert state == state.getZeroOrderState();
		assert state.getIndex() == boundary_.getIndex();
	}

	private double test(int index, List<List<State>> candidates, boolean print) {
		double score_sum = Double.NEGATIVE_INFINITY;
		if (candidates.isEmpty()) {
			return 0;
		} else {

			int state_index = 0;
			for (State state : candidates.get(0)) {

				State transition = state.getTransition(index);

				if (transition != null) {
					
					double rec = test(state_index,
							candidates.subList(1, candidates.size()), false);
					
					if (print) {
						System.err.format("%d %g %g %g\n", state_index, state.getScore(), transition.getScore(), rec);
					}
					
					double score = transition.getScore()
							+ state.getScore()
							+ rec;
					score_sum = Numerics.sumLogProb(score_sum, score);
				} else {
					System.err.format("%d null\n", state_index);
				}

				state_index++;
			}
		}
		
		lattice_[candidates_.size() - candidates.size()][index] = score_sum;
		return score_sum;
	}

	public void init() {
		lattice_ = new double[candidates_.size()][];

		for (int index = candidates_.size() - 1; index >= 0; index--) {
			List<State> previous_states = candidates_.get(index);

			List<State> states;
			if (index == 0) {
				states = Collections.singletonList(boundary_);
			} else {
				states = candidates_.get(index - 1);
			}

			lattice_[index] = new double[states.size()];
			for (int state_index = 0; state_index < states.size(); state_index++) {

				double score_sum = Double.NEGATIVE_INFINITY;

				int previous_state_index = 0;
				for (State previous_state : previous_states) {

					State transition = previous_state
							.getTransition(state_index);

					if (transition == null) {
						previous_state_index++;
						continue;
					}

					
					
					double score = previous_state.getScore()
							+ transition.getScore();

					if (index + 1 < candidates_.size()) {
						score += lattice_[index + 1][previous_state_index];
					}

					score_sum = Numerics.sumLogProb(score_sum, score);

					previous_state_index++;
				}

				lattice_[index][state_index] = score_sum;
			}
		}

		assert lattice_[0].length == 1;
	}

	double partitionFunction() {
		return lattice_[0][0];
	}

	public double get(int index, int state_index) {

		if (index == candidates_.size())
			return 0;

		return lattice_[index][state_index];
	}

	public void reinit() {
		lattice_ = new double[candidates_.size()][];

		for (int index = candidates_.size() - 1; index >= 0; index--) {
			List<State> states;
			if (index == 0) {
				states = Collections.singletonList(boundary_);
			} else {
				states = candidates_.get(index - 1);
			}
			lattice_[index] = new double[states.size()];
		}

		
		System.err.println(test(0, candidates_, false));
		
		System.err.println(partitionFunction());

		
		
		for (int index = candidates_.size() - 1; index >= 0; index--) {
			List<State> previous_states = candidates_.get(index);

			List<State> states;
			if (index == 0) {
				states = Collections.singletonList(boundary_);
			} else {
				states = candidates_.get(index - 1);
			}

			//lattice_[index] = new double[states.size()];
			for (int state_index = 0; state_index < states.size(); state_index++) {

				double score_sum = Double.NEGATIVE_INFINITY;

				int previous_state_index = 0;
				System.err.format("STATE\n");
				
				for (State previous_state : previous_states) {

					State transition = previous_state
							.getTransition(state_index);

					if (transition == null) {
						
						System.err.format("%d null\n", state_index);
						
					
						previous_state_index++;
						continue;
					}

					double score = previous_state.getScore()
							+ transition.getScore();

					System.err.format("%d %g %g", previous_state_index, previous_state.getScore(), transition.getScore());
					
					if (index + 1 < candidates_.size()) {
						score += lattice_[index + 1][previous_state_index];
						System.err.format(" %g\n", lattice_[index + 1][previous_state_index]);
					}
					
					System.err.format("\n");

					score_sum = Numerics.sumLogProb(score_sum, score);

					previous_state_index++;
				}

				try {
				diffTest(lattice_[index][state_index], score_sum);
				} catch (RuntimeException e) {
					
					System.err.println();
					System.err.println(candidates_.size());
					System.err.println(index);
					System.err.println(state_index);
					
					System.err.println();
					System.err.println();
					System.err.println(lattice_[index][state_index]);
					double f = test(state_index, candidates_.subList(index, candidates_.size()), true);
					System.err.println(lattice_[index][state_index]);
					System.err.println(f);
					System.err.println();
					System.err.println();
					
					throw e;
				}
				
				lattice_[index][state_index] = score_sum;
			}
		}

		assert lattice_[0].length == 1;
	}

	protected double diffTest(double a, double b) {
		double diff = Math.abs(a - b);
		if (diff > 1.e-10) {
			throw new RuntimeException(String.format("test failed: %g %g : %g",
					a, b, diff));
		}
		return diff;
	}
}

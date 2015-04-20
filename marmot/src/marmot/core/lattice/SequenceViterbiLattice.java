// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core.lattice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import marmot.core.State;
import marmot.core.Transition;
import marmot.lemma.ranker.RankerCandidate;
import marmot.util.HashableIntArray;


public class SequenceViterbiLattice implements ViterbiLattice {
	private LatticeEntry[][][] lattice_;
	private List<List<State>> candidates_;
	private State boundary_;
	private int beam_size_;
	private boolean initilized_;

	public SequenceViterbiLattice(List<List<State>> candidates, State boundary, int beam_size) {
		candidates_ = candidates;
		boundary_ = boundary;
		beam_size_ = beam_size;
		initilized_ = false;
	}

	public void init() {
		
		if (initilized_) {
			return;
		}
		initilized_ = true;
		
		lattice_ = new LatticeEntry[candidates_.size()][][];
		PriorityQueue<LatticeEntry> queue = new PriorityQueue<LatticeEntry>();
		List<State> previous_states = Collections.singletonList(boundary_);
		for (int index = 0; index < candidates_.size(); index++) {
			List<State> states = candidates_.get(index);
			lattice_[index] = new LatticeEntry[states.size()][];
			int state_index = 0;
			for (State state : states) {
				queue.clear();
			
				double state_score = state.getScore();
				State zero_order_state = state.getZeroOrderState();
				if (zero_order_state.getLemmaCandidates() != null) {
					double score = state_score - zero_order_state.getScore() + zero_order_state.getRealScore();
					state_score = Double.NEGATIVE_INFINITY;
					for (RankerCandidate candidate : zero_order_state.getLemmaCandidates()) {
						double candidate_score = score + candidate.getScore();
						state_score = Math.max(state_score, candidate_score);
					}
				}
				
				for (int previous_state_index = 0; previous_state_index < previous_states
						.size(); previous_state_index++) {

					State transition = state.getTransition(previous_state_index);
					
					if (transition == null) {
						continue;
					}
					
					double score = state_score + transition.getScore();
					
					if (index > 0) {
						score += lattice_[index - 1][previous_state_index][0]
								.getScore();
					}

					queue.add(new LatticeEntry(score, previous_state_index));
				}
				
				int length = Math.min(beam_size_, queue.size());
				assert length > 0;
				lattice_[index][state_index] = new LatticeEntry[length];

				for (int rank = 0; rank < length; rank++) {
					LatticeEntry entry = queue.poll();
					
					if (entry == null)
						break;
					
					lattice_[index][state_index][rank] = entry;
				}
				state_index ++;
			}
			previous_states = states;
		}
	}

	public Hypothesis getViterbiSequence() {
		init();
		int[] signature_array = new int[candidates_.size() - 1];
		HashableIntArray signature = new HashableIntArray(signature_array);
		return getSequenceBySignature(signature);
	}

	public Hypothesis getSequenceBySignature(HashableIntArray signature) {
		init();
		List<Integer> list = new LinkedList<Integer>();
		int index = candidates_.size() - 1;
		int state_index = 0;
		list.add(0);
		Double score = null;

		int[] signature_array = signature.getArray();
		
		while (index >= 1) {
			int rank = signature_array[index - 1];

			if (rank >= lattice_[index][state_index].length) {
				return null;
			}
			
			LatticeEntry entry = lattice_[index][state_index][rank];
			if (entry == null) {
				return null;
			}

			if (score == null) {
				score = entry.getScore();
			}

			if (rank != 0) {
				score += entry.getScore()
						- lattice_[index][state_index][0].getScore();
			}

			state_index = entry.getPreviousStateIndex();
			index--;
			list.add(state_index);
		}

		if (score == null) {
			return null;
		}
		
		Collections.reverse(list);
		return new Hypothesis(list, score, signature);
	}

	public List<Hypothesis> getNbestSequences() {
		init();
		List<Hypothesis> list = new LinkedList<Hypothesis>();

		HashableIntArray signature = new HashableIntArray(new int[candidates_.size() - 1]);
		PriorityQueue<Hypothesis> queue = new PriorityQueue<Hypothesis>();
		Set<HashableIntArray> used_signatures = new HashSet<>();
		queue.add(getSequenceBySignature(signature));
		used_signatures.add(signature);

		while (list.size() < beam_size_) {
			Hypothesis h = queue.poll();

			if (h == null) {
				break;
			}

			list.add(h);
			signature = h.getSignature();
			int [] signature_array = signature.getArray();

			for (int index = 0; index < signature_array.length; index++) {
				int[] new_signature_array = new int[signature_array.length];
				System.arraycopy(signature_array, 0, new_signature_array, 0,
						signature_array.length);
				new_signature_array[index]++;
				
				HashableIntArray new_signature = new HashableIntArray(new_signature_array);

				if (!used_signatures.contains(new_signature)) {
					used_signatures.add(new_signature);
					h = getSequenceBySignature(new_signature);
					if (h != null) {
						queue.add(h);
					}
				}
			}
		}
		return list;
	}

	public void findGoldSequence(List<Integer> path) {
		init();
		assert path.size() == candidates_.size();
		assert path.size() == lattice_.length;
		
		for (int index = path.size() - 1; index > 0; index --) {
			int state_index =  path.get(index);
			int real_previous_state_index = path.get(index - 1);

			boolean found_index = false;
			for (LatticeEntry entry : lattice_[index][state_index]) {
				if (entry == null) {
					break;
				}

				int previous_state_index = entry.getPreviousStateIndex();
				
				if (previous_state_index == real_previous_state_index) {
					found_index = true;
					break;
				}
				
			}
			
			if (!found_index)
				System.err.format("%s index = %d p_index = %d lattice entries = %s\n", candidates_.get(index).get(state_index), index, real_previous_state_index, Arrays.toString(lattice_[index][state_index]));
		}
		
	}
	
	public List<List<State>> prune() {
		init();
		List<List<State>> candidates = getCandidates();

		List<Set<Integer>> candidate_sets = new ArrayList<Set<Integer>>(
				candidates.size());
		for (int index = 0; index < candidates.size(); index++) {
			candidate_sets.add(new HashSet<Integer>());
		}

		for (Hypothesis h : getNbestSequences()) {
			int index = 0;
			int previous_state_index = 0;
			for (int state_index : h.getStates()) {
				int previous_num_candidates = (index - 1 >= 0) ? candidates
						.get(index - 1).size() : 1;
				candidate_sets.get(index).add(
						state_index * previous_num_candidates
								+ previous_state_index);
				previous_state_index = state_index;
				index++;
			}
		}

		List<List<State>> new_candidates = new ArrayList<List<State>>(
				candidates.size());

		int[] index_map = null;

		for (int index = 0; index < candidates.size(); index++) {
			Set<Integer> candidate_set = candidate_sets.get(index);
			int[] new_index_map = new int[candidates.get(index).size()];
			Arrays.fill(new_index_map, -1);
			
			List<State> states = new ArrayList<State>(candidate_set.size());
			for (int encoded_indexes : candidate_set) {

				int previous_num_candidates = (index - 1 >= 0) ? candidates
						.get(index - 1).size() : 1;
				int state_index = encoded_indexes / previous_num_candidates;
				int previous_state_index = encoded_indexes
						% previous_num_candidates;

				int new_state_index = new_index_map[state_index];

				if (new_state_index < 0) {
					new_state_index = states.size();
					new_index_map[state_index] = new_state_index;
					State state = candidates.get(index).get(state_index);
					if (index > 0) {
						state = state.copy();
						Transition[] new_transitions = new Transition[new_candidates.get(
								index - 1).size()];
						state.setTransitions(new_transitions);

					}
					states.add(state);
				}
				
				if (index > 0) {
					State old_state = candidates.get(index).get(state_index);
					State[] transitions = old_state.getTransitions();
					
					State state = states.get(new_state_index);
					State[] new_transitions = state.getTransitions();
					
					new_transitions[index_map[previous_state_index]] = transitions[previous_state_index];
				}
				
			}
			
			new_candidates.add(states);
			index_map = new_index_map;
		}
		return new_candidates;
	}

	@Override
	public List<List<State>> getCandidates() {
		return candidates_;
	}
}

// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import marmot.core.lattice.Hypothesis;
import marmot.core.lattice.SequenceSumLattice;
import marmot.core.lattice.SequenceViterbiLattice;
import marmot.core.lattice.SumLattice;
import marmot.core.lattice.ViterbiLattice;
import marmot.core.lattice.ZeroOrderSumLattice;
import marmot.core.lattice.ZeroOrderViterbiLattice;

public class SimpleTagger implements Tagger {
	private static final long serialVersionUID = 1L;
	private Model model_;
	private WeightVector weight_vector_;
	private int num_level_;

	private double[][] threshs_;
	private double[] candidates_per_state_;
	private double[][] num_states_;
	private double[][] length_;
	private int order_;

	private boolean prune_;
	private int effective_order_;
	private int beam_size_;
	private boolean oracle_;
	private final int AVERAGE_NUMBER_OF_CANDIDATES = 5;

	public SimpleTagger(Model model, int order, WeightVector weight_vector) {
		order_ = order;
		model_ = model;
		prune_ = model.getOptions().getPrune();
		beam_size_ = model.getOptions().getBeamSize();
		oracle_ = model.getOptions().getOracle();
		effective_order_ = Math.min(order, model.getOptions()
				.getEffectiveOrder());
		weight_vector_ = weight_vector;

		candidates_per_state_ = model.getOptions().getCandidatesPerState();

		int levels = model_.getTagTables().size();
		num_level_ = levels;

		threshs_ = new double[levels][getOrder() + 1];
		length_ = new double[levels][getOrder() + 1];
		num_states_ = new double[levels][getOrder() + 1];
		for (int level = 0; level < threshs_.length; level++) {
			Arrays.fill(threshs_[level], model.getOptions().getProbThreshold());
			Arrays.fill(length_[level], 0);
			Arrays.fill(num_states_[level], 0);
		}
	}

	private void addTransitions(List<List<State>> states, int level, int order) {
		List<State> last_states = Collections.singletonList((State) model_
				.getBoundaryState(level));
		for (int index = 0; index < states.size(); index++) {
			List<State> current_states = states.get(index);
			Transition[][] transitions = new Transition[last_states.size()][current_states
					.size()];

			int from_index = 0;
			for (State last_state : last_states) {

				FeatureVector vector = weight_vector_
						.extractTransitionFeatures(last_state);

				int to_index = 0;

				for (State state : current_states) {

					if (last_state.canTransitionTo(state)) {
						Transition transition = new Transition(last_state,
								state, order);
						transition.setVector(vector);

						double score = 0.0;
						State run = state;
						while (run != null) {
							score += weight_vector_.dotProduct(run, vector);
							run = run.getSubLevelState();
						}

						transition.setScore(score);
						transitions[from_index][to_index] = transition;
					}

					to_index++;
				}

				from_index++;
			}

			int to_index = 0;
			for (State state : current_states) {
				boolean found_transition = false;
				Transition[] transition_row = new Transition[last_states.size()];
				for (from_index = 0; from_index < last_states.size(); from_index++) {
					transition_row[from_index] = transitions[from_index][to_index];

					if (transition_row[from_index] != null)
						found_transition = true;
				}
				assert (found_transition);
				state.setTransitions(transition_row);
				to_index++;
			}

			last_states = current_states;
		}
	}

	protected List<List<State>> increaseOrder(List<List<State>> states,
			int level) {
		List<List<State>> new_state_candidates = new ArrayList<List<State>>(
				states.size() + 1);

		for (int index = 0; index < states.size(); index++) {
			int num_previous_states;
			if (index == 0) {
				num_previous_states = 1;
			} else {
				num_previous_states = states.get(index - 1).size();
			}

			List<State> current_states = states.get(index);
			List<State> new_states = new ArrayList<State>(current_states.size()
					* num_previous_states);

			for (State state : current_states) {

				Transition[] transitions = state.getTransitions();
				state.setTransitions(null);

				assert num_previous_states <= transitions.length;

				for (int previous_state_index = 0; previous_state_index < num_previous_states; previous_state_index++) {
					Transition t = transitions[previous_state_index];
					if (t == null) {
						continue;
					}

					t.setScore(t.getScore() + state.getScore());

					new_states.add(t);
					t.getSubOrderState().setTransitions(null);
					assert t.check();
				}

			}

			assert !new_states.isEmpty();
			new_state_candidates.add(new_states);
		}

		new_state_candidates.add(Collections.singletonList(model_
				.getBoundaryState(level)));
		return new_state_candidates;
	}

	private boolean cache_feature_vector_ = false;
	private Result result_;

	protected List<List<State>> getStates(Sequence sequence) {
		List<List<State>> candidates = new ArrayList<List<State>>(
				sequence.size() + 1);
		for (int index = 0; index < sequence.size(); index++) {

			Token token = sequence.get(index);

			FeatureVector vector = token.getVector();
			if (vector == null) {
				vector = weight_vector_.extractStateFeatures(sequence, index);

				if (cache_feature_vector_)
					token.setVector(vector);
			}

			int[] tag_indexes = model_.getTagCandidates(sequence, index, null);
			List<State> states = new ArrayList<State>(tag_indexes.length);
			for (int tag_index : tag_indexes) {

				if (tag_index == -1)
					break;

				State state = new State(tag_index);
				state.setVector(vector);
				state.setScore(weight_vector_.dotProduct(state, vector));
				model_.setLemmaCandidates(token, state, true);
				states.add(state);
			}
			assert states.size() > 0;
			candidates.add(states);
		}
		candidates.add(Collections.singletonList(model_.getBoundaryState(0)));
		return candidates;
	}

	@Override
	public String setThresholds(boolean print) {
		StringBuilder sb = null;

		if (print) {
			sb = new StringBuilder();
		}

		for (int level = 0; level < num_states_.length; level++) {
			for (int order = 0; order < num_states_[level].length; order++) {

				if (length_[level][order] > 0) {

					double num_states = num_states_[level][order]
							/ length_[level][order];

					int effective_order = Math.min(order,
							candidates_per_state_.length - 1);
					double want = candidates_per_state_[effective_order];

					if (Math.abs(num_states - want) > 1e-1) {
						if (num_states > want) {
							threshs_[level][order] += 0.1 * threshs_[level][order];
						} else {
							threshs_[level][order] -= 0.1 * threshs_[level][order];
						}
					}

					if (print) {
						sb.append(' ');
						sb.append(num_states);
					}
					num_states_[level][order] = 0;
					length_[level][order] = 0;

				}
			}

			if (print) {
				sb.append('\n');
			}
		}

		if (print)
			return sb.toString();

		return null;
	}

	private List<List<State>> increaseLevel(List<List<State>> candidates,
			Sequence sentence) {
		List<List<State>> new_candidates = new ArrayList<List<State>>(
				candidates.size());
		final int average_size = AVERAGE_NUMBER_OF_CANDIDATES;
		int index = 0;
		for (List<State> current_states : candidates) {
			List<State> new_current_states;
			if (index < candidates.size() - 1) {
				new_current_states = new ArrayList<State>(current_states.size()
						* average_size);
				for (State state : current_states) {
					FeatureVector vector = weight_vector_
							.extractStateFeatures(state);
					assert state.getTransitions() == null;
					int[] tag_indexes = model_.getTagCandidates(sentence,
							index, state);
					for (int tag_index : tag_indexes) {

						if (tag_index == -1) {
							break;
						}

						assert state.getOrder() == 1;

						State new_state = new State(tag_index, state);
						new_state.setVector(vector);
						new_state.setScore(weight_vector_.dotProduct(new_state,
								vector) + state.getRealScore());
						model_.setLemmaCandidates(new_state, true);
						new_current_states.add(new_state);
					}
				}
			} else {
				new_current_states = Collections
						.singletonList(model_.getBoundaryState(current_states
								.get(0).getLevel() + 1));
			}
			new_candidates.add(new_current_states);
			index++;
		}
		return new_candidates;
	}

	protected void incrementStateCounter(int level, int order,
			List<List<State>> candidates) {
		int num_states = 0;
		for (List<State> states : candidates) {
			num_states += states.size();
		}

		int length = candidates.size();

		num_states_[level][order] += num_states;
		length_[level][order] += length;
	}

	@Override
	public SumLattice getSumLattice(boolean train, Sequence sentence) {
		int order = getOrder();

		List<List<State>> candidates = null;
		SumLattice lattice = null;

		for (int level = 0; level < getNumLevels(); level++) {
			if (level == 0) {
				candidates = getStates(sentence);

			} else {
				candidates = lattice.getZeroOrderCandidates(prune_);
				incrementStateCounter(level - 1, lattice.getOrder(), candidates);
				if (train
						&& testForGoldCandidates(sentence, candidates, lattice) == null) {
					return lattice;
				}

				int old_size = candidates.size();

				candidates = increaseLevel(candidates, sentence);

				assert candidates.size() == old_size;
				for (List<State> states : candidates) {
					assert !states.isEmpty();
				}
			}

			lattice = new ZeroOrderSumLattice(candidates, threshs_[level][0], oracle_);
			
			if (oracle_ || train)
				lattice.setGoldCandidates(getGoldIndexes(sentence,
						lattice.getCandidates()));

			int effective_order = effective_order_;
			if (level + 1 == getNumLevels()) {
				effective_order = order;
			}

			for (int current_order = 0; current_order < effective_order; current_order++) {
				if (prune_) {
					candidates = lattice.prune();

					incrementStateCounter(level, current_order,
							lattice.getZeroOrderCandidates(true));
					assert candidates.size() > 0;
				}

				if (current_order == 0) {

					if (level == 0) {
						int index = 0;
						for (List<State> states : candidates) {
							if (index + 1 < candidates.size()) {
								// Last state is boundary state
								// Add lemma scores with pos features
								for (State state : states) {
									model_.setLemmaCandidates(
											sentence.get(index), state, false);
								}
							}
							index++;
						}
					} else if (level + 1 == getNumLevels()) {
						int index = 0;
						for (List<State> states : candidates) {
							if (index + 1 < candidates.size()) {
								// Last state is boundary state
								// Add lemma scores with morph features
								for (State state : states) {
									model_.setLemmaCandidates(state, false);
								}
							}
							index++;
						}
					}
				}

				/*
				 * During training, if gold sequence is not among the new
				 * candidates return the lattice immediately to do an early
				 * update.
				 */

				if (train
						&& testForGoldCandidates(sentence, candidates, lattice) == null) {
					return lattice;
				}

				if (current_order > 0) {
					candidates = increaseOrder(candidates, level);
				}

				addTransitions(candidates, level, current_order + 2);

				lattice = new SequenceSumLattice(candidates,
						model_.getBoundaryState(level),
						threshs_[level][current_order + 1], current_order + 1,
						false);

				if (oracle_ || train)
					lattice.setGoldCandidates(getGoldIndexes(sentence,
							lattice.getCandidates()));
			}
		}

		assert lattice.getCandidates().size() >= sentence.size();
		return lattice;
	}

	private List<Integer> testForGoldCandidates(Sequence sentence,
			List<List<State>> candidates, SumLattice lattice) {
		List<Integer> gold_candidates = getGoldIndexes(sentence, candidates);
		if (gold_candidates != null) {
			return gold_candidates;
		}

		return null;
	}

	public int getOrder() {
		return order_;
	}

	@Override
	public int getNumLevels() {
		return num_level_;
	}

	@Override
	public List<Integer> getGoldIndexes(Sequence sequence,
			List<List<State>> candidates) {

		List<Integer> list = new ArrayList<Integer>(candidates.size());

		int last_candidate_index = 0;
		for (int index = 0; index < candidates.size(); index++) {
			List<State> current_candidates = candidates.get(index);
			List<Integer> current_candidate_indexes = new ArrayList<Integer>(
					current_candidates.size());
			for (int candidate_index = 0; candidate_index < current_candidates
					.size(); candidate_index++) {
				current_candidate_indexes.add(candidate_index);
			}

			int max_level = current_candidates.get(0).getZeroOrderState()
					.getLevel();

			for (int level = max_level; level >= 0; level--) {
				List<Integer> new_current_candidate_indexes = new ArrayList<Integer>(
						current_candidate_indexes.size());

				int gold_tag_index;
				if (index < sequence.size()) {
					gold_tag_index = sequence.get(index).getTagIndexes()[level];
				} else {
					gold_tag_index = model_.getBoundaryIndex();
				}

				for (int state_index = 0; state_index < current_candidate_indexes
						.size(); state_index++) {
					int candidate_index = current_candidate_indexes
							.get(state_index);
					State state = current_candidates.get(candidate_index);

					if (level == max_level) {
						// check transition!

						boolean valid = (state.getTransitions() == null || state
								.getTransition(last_candidate_index) != null);

						if (!valid) {
							continue;
						}

					}

					if (gold_tag_index == state.getZeroOrderState()
							.getSubLevel(max_level - level).getIndex()) {
						new_current_candidate_indexes.add(candidate_index);
					}

				}

				current_candidate_indexes = new_current_candidate_indexes;
				if (current_candidate_indexes.isEmpty()) {
					return null;
				}

			}

			assert current_candidate_indexes.size() == 1;
			int gold_candidate_index = current_candidate_indexes.get(0);
			list.add(gold_candidate_index);
			last_candidate_index = gold_candidate_index;
		}

		return list;
	}

	@Override
	public Model getModel() {
		return model_;
	}

	@Override
	public WeightVector getWeightVector() {
		return weight_vector_;
	}

	@Override
	public List<List<String>> tag(Sequence sentence) {
		List<int[]> indexes = tag_(sentence);

		List<List<String>> strings = new ArrayList<List<String>>(indexes.size());

		for (int[] array : indexes) {
			strings.add(indexesToStrings(array));
		}

		return strings;
	}

	protected List<String> indexesToStrings(int[] indexes) {
		List<String> sarray = new ArrayList<String>(indexes.length);

		int level = 0;
		for (int index : indexes) {
			sarray.add(model_.getTagTables().get(level).toSymbol(index));
			level++;
		}

		return sarray;
	}

	protected int[] stateToIndexes(State state) {

		int num_levels = state.getLevel() + 1;

		int[] indexes = new int[num_levels];
		for (int level = num_levels - 1; level >= 0; level--) {
			assert state != null;
			assert state.getIndex() >= 0;
			indexes[level] = state.getIndex();
			state = state.getSubLevelState();
		}
		return indexes;
	}

	protected List<State> tag_states(Sequence sequence) {
		List<State> list = new ArrayList<State>(sequence.size());
		SumLattice sum_lattice = getSumLattice(false, sequence);

		List<List<State>> candidates = sum_lattice.getCandidates();

		ViterbiLattice lattice;
		if (sum_lattice instanceof ZeroOrderSumLattice) {
			lattice = new ZeroOrderViterbiLattice(candidates, beam_size_,
					model_.getMarganlizeLemmas());
		} else {
			lattice = new SequenceViterbiLattice(candidates,
					model_.getBoundaryState(getNumLevels() - 1), beam_size_,
					model_.getMarganlizeLemmas());
		}

		Hypothesis h = lattice.getViterbiSequence();
		List<Integer> state_indexes = h.getStates();

		for (int index = 0; index < sequence.size(); index++) {
			int candidate_index = state_indexes.get(index);
			List<State> token_candidates = candidates.get(index);
			State state = token_candidates.get(candidate_index);
			state = state.getZeroOrderState();
			list.add(state);
		}

		return list;
	}

	protected List<int[]> tag_(Sequence sequence) {
		List<int[]> list = new ArrayList<int[]>(sequence.size());

		List<State> states = tag_states(sequence);

		for (State state : states) {
			int[] indexes = stateToIndexes(state);
			list.add(indexes);
		}

		return list;
	}

	public void setMaxLevel(int level) {
		num_level_ = level;
	}

	@Override
	public void setResult(Result result) {
		result_ = result;
	}

	@Override
	public Result getResult() {
		return result_;
	}

}

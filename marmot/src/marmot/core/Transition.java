// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.core;


public class Transition extends State {
	private State previous_state_;
	private State state_;
	private int order_;
	
	public Transition(State previous_state, State state, int order) {
		super();
		previous_state_ = previous_state;
		state_ = state;
		order_ = order;
	}

	@Override
	public int getOrder() {
		return order_;
	}

	@Override
	public boolean canTransitionTo(State other) {
		if (other.getOrder() != getOrder()) {
			assert other.getOrder() == 1;
			assert other.getIndex() == 0;
			return true;
		}

		State next = state_;
		State previous = ((Transition) other).previous_state_;
		
		/*System.err.println(this);
		System.err.println(other);
		System.err.println(previous);
		System.err.println(next);
		System.err.println(previous.equalIndexes(next));
		System.err.println();*/
		
		return previous.equalIndexes(next);
	}

	@Override
	public boolean equalIndexes(State other) {
		assert other instanceof Transition;
		Transition t = (Transition) other;
		return t.state_.equalIndexes(state_)
				&& t.previous_state_.equalIndexes(previous_state_);
	}
	
	@Override
	public void incrementEstimatedCounts(double d) {
		estimated_count_ += d;
		state_.incrementEstimatedCounts(d);
	}

	@Override
	public void updateWeights(WeightVector weights) {
		if (estimated_count_ != 0.0) {
			weights.updateWeights(this, estimated_count_, true);
			state_.updateWeights(weights);
			estimated_count_ = 0.0;
		}
	}
	
	@Override
	public State getZeroOrderState() {
		return state_.getZeroOrderState();
	}

	@Override
	public State getPreviousSubOrderState() {
		return previous_state_;
	}
	
	@Override
	public State getSubOrderState() {
		return state_;
	}

	@Override
	public int getIndex() {
		return state_.getIndex();
	}
	
	@Override
	public State getSubLevelState() {
		return state_.getSubLevelState();
	}
	
	@Override
	public void setSubLevelState(State sub_level_state) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public State getSubLevel(int depth) {
		return state_.getSubLevel(depth);
	}
	
	@Override
	public String toString() {
		return "(" + previous_state_.toString() + " " + getZeroOrderState().toString() + ")";
	}
	
	@Override
	public int getLevel() {
		return state_.getLevel();
	}
	
	@Override
	public boolean check() {
		return previous_state_.check() && state_.check();
	}
	
	@Override
	public State copy() {
		return super.copy(new Transition(previous_state_, state_, order_));
	}
	
}

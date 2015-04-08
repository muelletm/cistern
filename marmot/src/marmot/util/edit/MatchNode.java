package marmot.util.edit;

import marmot.util.Counter;

public class MatchNode implements EditTree {
	private EditTree left_;
	private EditTree right_;
	private Match match_;

	public MatchNode(Match m, EditTree left, EditTree right) {
		match_ = m;
		left_ = left;
		right_ = right;
	}

	@Override
	public String toString() {
		return String.format("(%s %s %s)", left_, match_, right_);
	}

	@Override
	public double getCost(EditTreeBuilder builder) {
		double cost = 0;
		
		if (left_ != null) {
			cost += left_.getCost(builder);
		}
		
		if (right_ != null) {
			cost += right_.getCost(builder);
		}
		
		return cost;
	}

	public EditTree getLeft() {
		return left_;
	}
	
	public EditTree getRight() {
		return right_;
	}
	
	public Match getMatch() {
		return match_;
	}

	@Override
	public int getInputLength() {
		return left_.getInputLength() + match_.getLength() + right_.getInputLength();
	}

	@Override
	public int getOutputLength() {
		return left_.getOutputLength() + match_.getLength() + right_.getOutputLength();
	}
	
	public void increment(Counter<String> counter) {
		if (left_ != null)
			left_.increment(counter);
		if (right_ != null)
			right_.increment(counter);
	}
}

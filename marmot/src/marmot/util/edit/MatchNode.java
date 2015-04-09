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
		
		StringBuilder sb = new StringBuilder();
		
		sb.append('(');
		
		if (left_ != null) {
			sb.append(left_);
		}
		
		if (sb.length() > 1) {
			sb.append(" ");
		}
		
		if (match_ == null) {
			sb.append("M");	
		} else {
			sb.append(String.format("M(%d)", match_.getLength()));
		}
		
		
		
		
		if (right_ != null) {
			sb.append(" ");
			sb.append(right_);
		}
		
		sb.append(')');
		
		return sb.toString();
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
		int length = 0;
		if (left_ != null)
			length += left_.getInputLength();
		
		length += match_.getLength();
		
		if (right_ != null)
			length += right_.getInputLength();
		
		return length;
	}

	@Override
	public int getOutputLength() {
		int length = 0;
		if (left_ != null)
			length += left_.getOutputLength();
		
		length += match_.getLength();
		
		if (right_ != null)
			length += right_.getOutputLength();
		
		return length;
	}
	
	public void increment(Counter<String> counter) {
		if (left_ != null)
			left_.increment(counter);
		if (right_ != null)
			right_.increment(counter);
	}

	@Override
	public String apply(String input, int start, int end) {
		int left_length = 0;
		String left = "";
		if (left_ != null) {
			left_length = left_.getInputLength();
			
			if (start + left_length > end) {
				return null;
			}
			
			left = left_.apply(input, start, start + left_length);
			
			if (left == null) {
				return null;
			}
		}
		
		int right_length = 0;
		String right = "";
		if (right_ != null) {
			right_length = right_.getInputLength();
			
			if (end - right_length < start) {
				return null;
			}
			
			right = right_.apply(input, end - right_length, end);
			if (right == null) {
				return null;
			}
		}
		
		int middle_length = end - start - left_length - right_length;
		if (middle_length <= 0) {
			return null;
		}
		
		String middle = input.substring(start + left_length, start + left_length + middle_length);
		return left + middle + right;
	}

	@Override
	public void prepareHashing() {
		match_ = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left_ == null) ? 0 : left_.hashCode());
		result = prime * result + ((match_ == null) ? 0 : match_.hashCode());
		result = prime * result + ((right_ == null) ? 0 : right_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchNode other = (MatchNode) obj;
		if (left_ == null) {
			if (other.left_ != null)
				return false;
		} else if (!left_.equals(other.left_))
			return false;
		if (match_ == null) {
			if (other.match_ != null)
				return false;
		} else if (!match_.equals(other.match_))
			return false;
		if (right_ == null) {
			if (other.right_ != null)
				return false;
		} else if (!right_.equals(other.right_))
			return false;
		return true;
	}


}

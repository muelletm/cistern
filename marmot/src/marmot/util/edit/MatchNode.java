package marmot.util.edit;

import marmot.util.Counter;

public class MatchNode implements EditTree {
	private EditTree left_;
	private EditTree right_;
	
	private int left_length_;
	private int right_length_;

	public MatchNode(EditTree left, EditTree right, int left_length, int right_length) {
		left_length_ = left_length;
		right_length_ = right_length;
		left_ = left;
		right_ = right;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append('(');
		
		if (left_ != null) {
			sb.append(left_length_);
			sb.append(left_);
		}
		
		if (sb.length() > 1) {
			sb.append(" ");
		}
		
		if (right_ != null) {
			sb.append(" ");
			sb.append(right_length_);
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
	
	public void increment(Counter<String> counter) {
		if (left_ != null)
			left_.increment(counter);
		if (right_ != null)
			right_.increment(counter);
	}

	@Override
	public String apply(String input, int start, int end) {
		String left = "";
		if (left_ != null) {
			
			if (start + left_length_ > end) {
				return null;
			}
			
			left = left_.apply(input, start, start + left_length_);
			
			if (left == null) {
				return null;
			}
		}
		
		String right = "";
		if (right_ != null) {
			if (end - right_length_ < start) {
				return null;
			}
			
			right = right_.apply(input, end - right_length_, end);
			if (right == null) {
				return null;
			}
		}
		
		int middle_length = end - start - left_length_ - right_length_;
		if (middle_length <= 0) {
			return null;
		}
		
		String middle = input.substring(start + left_length_, start + left_length_ + middle_length);
		return left + middle + right;
	}

	public int getLeftInputLength() {
		return left_length_;
	}

	public int getRightnputLength() {
		return right_length_;
	}

	@Override
	public int getFixedInputLength() {
		int fixed_length = 0;
		if (left_ != null) {
			fixed_length += left_.getFixedInputLength();
		}
		if (right_ != null) {
			fixed_length += right_.getFixedInputLength();
		}
		return fixed_length;
	}

	@Override
	public int getFixedOutputLength() {
		int fixed_length = 0;
		if (left_ != null) {
			fixed_length += left_.getFixedOutputLength();
		}
		if (right_ != null) {
			fixed_length += right_.getFixedOutputLength();
		}
		return fixed_length;
	}

	int hash_code_ = 0;
	
	@Override
	public int hashCode() {
		if (hash_code_ == 0) {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((left_ == null) ? 0 : left_.hashCode());
			result = prime * result + left_length_;
			result = prime * result + ((right_ == null) ? 0 : right_.hashCode());
			result = prime * result + right_length_;
			hash_code_ = result;
		}
		return hash_code_;
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
		if (left_length_ != other.left_length_)
			return false;
		if (right_ == null) {
			if (other.right_ != null)
				return false;
		} else if (!right_.equals(other.right_))
			return false;
		if (right_length_ != other.right_length_)
			return false;
		return true;
	}

}

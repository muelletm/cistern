package marmot.util.edit;

import marmot.util.Counter;

public class ReplaceNode implements EditTree {

	private String input_;
	private String output_;

	public ReplaceNode(String input, String output) {
		input_ = input;
		output_ = output;
	}

	@Override
	public String toString() {
		return String.format("r(%s, %s)", input_, output_);
	}

	@Override
	public double getCost(EditTreeBuilder builder) {
		double count = builder.getCounter().count(toString()) + 1;
		double cost = (input_.length() + output_.length()) / count;
		return cost;
	}

	@Override
	public void increment(Counter<String> counter) {
		counter.increment(toString(), 1.0);
	}

	@Override
	public String apply(String input, int start, int end) {
		assert start >= 0;
		assert end <= input.length();
		
		int length = end - start;
		
		if (length != input_.length()) {
			return null;
		}
		
		if (!input.substring(start, end).equals(input_)) {
			return null;
		}
		
		return output_;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((input_ == null) ? 0 : input_.hashCode());
		result = prime * result + ((output_ == null) ? 0 : output_.hashCode());
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
		ReplaceNode other = (ReplaceNode) obj;
		if (input_ == null) {
			if (other.input_ != null)
				return false;
		} else if (!input_.equals(other.input_))
			return false;
		if (output_ == null) {
			if (other.output_ != null)
				return false;
		} else if (!output_.equals(other.output_))
			return false;
		return true;
	}

	@Override
	public int getFixedInputLength() {
		return input_.length();
	}

	@Override
	public int getFixedOutputLength() {
		return output_.length();
	}
	
}

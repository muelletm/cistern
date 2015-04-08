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
	public int getInputLength() {
		return input_.length();
	}

	@Override
	public int getOutputLength() {
		return output_.length();
	}

	@Override
	public void increment(Counter<String> counter) {
		counter.increment(toString(), 1.0);
		System.err.println(counter);
	}
	
}

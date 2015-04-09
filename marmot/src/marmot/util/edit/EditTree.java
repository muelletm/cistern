package marmot.util.edit;

import marmot.util.Counter;

public interface EditTree {

	public double getCost(EditTreeBuilder builder);
	
	public String apply(String input, int start, int end);
	
	public void increment(Counter<String> counter);

	public int getFixedInputLength();

	public int getFixedOutputLength();
		
}

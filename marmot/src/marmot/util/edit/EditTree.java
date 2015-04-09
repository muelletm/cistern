package marmot.util.edit;

import marmot.util.Counter;

public interface EditTree {

	public double getCost(EditTreeBuilder builder);
	public int getInputLength();
	public int getOutputLength();
	
	public String apply(String input, int start, int end);
	
	public void increment(Counter<String> counter);
	
	public void prepareHashing();
	
}

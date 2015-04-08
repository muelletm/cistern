package marmot.util.edit;

import marmot.util.Counter;

public interface EditTree {

	public double getCost(EditTreeBuilder builder);
	public int getInputLength();
	public int getOutputLength();
	
	public void increment(Counter<String> counter);
	
}

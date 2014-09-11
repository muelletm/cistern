package marmot.core;

public interface FloatWeights {

	public int getFloatIndex(int feature, int tag_index);
	public double getFloatWeight(int index);
	public void updateFloatWeight(int index, double value);
	
}

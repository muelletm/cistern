package marmot.core;

public class ZeroFloatFeatureVector implements FloatFeatureVector {

	private static final long serialVersionUID = 1L;
	private int dim_;

	public ZeroFloatFeatureVector(int dim) {
		dim_ = dim;
	}

	@Override
	public int getDim() {
		return dim_;
	}

	@Override
	public void updateFloatWeight(FloatWeights weights,
			int tag_index, int offset, double update) {
	}

	@Override
	public double getDotProduct(FloatWeights weights, int tag_index,
			int offset) {
		return 0;
	}

}

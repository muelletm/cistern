package marmot.lemma.toutanova;

public class IndexUpdater extends IndexConsumer {

	private double[] weights_;
	private double update_;
	
	public IndexUpdater(double[] weights) {
		weights_ = weights;
	}
	
	public void setUpdate(double update) {
		update_ = update;
	}
	
	@Override
	public void consume(int index) {
		if (index >= 0) {
			weights_[index] += update_;
		}
	}

	@Override
	protected boolean getInsert() {
		return true;
	}

	public void setWeights(double[] weights) {
		weights_ = weights;
	}
	


}

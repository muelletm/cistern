package marmot.lemma.toutanova;

public class IndexScorer extends IndexConsumer {

	double score_;
	double[] weights_;
	
	public IndexScorer(double[] weights) {
		weights_ = weights;
	}
	
	public void reset() {
		score_ = 0.0;
	}
	
	@Override
	public void consume(int index) {
		if (index >= 0) {
			score_ += weights_[index];
		}
	}

	public double getScore() {
		return score_;
	}

	@Override
	protected boolean getInsert() {
		return false;
	}

	public void setWeights(double[] weights) {
		weights_ = weights;
	}

}

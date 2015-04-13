package marmot.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class DynamicWeights implements Serializable {

	private double[] weights_;
	private Random random_;

	public DynamicWeights(Random random) {
		weights_ = new double[0];
		random_ = random;
	}

	public double[] getWeights() {
		return weights_;
	}

	public void setWeights(double[] weight) {
		weights_ = weight;
	}

	public double get(int index) {
		checkCapacity(index);
		return weights_[index];
	}

	private void checkCapacity(int index) {
		if (index >= weights_.length) {
			int old_length = weights_.length;
			weights_ = Arrays.copyOf(weights_,
					Math.max(index + 10, (weights_.length * 2) / 3));
			if (random_ != null) {
				for (int i = old_length; i < weights_.length; i++) {
					weights_[i] = random_.nextGaussian();
				}
			}
		}
	}

	public void incremen(int index, double update) {
		checkCapacity(index);
		weights_[index] += update;
	}
	
	public void set(int index, double update) {
		checkCapacity(index);
		weights_[index] = update;
	}

	public int getLength() {
		return weights_.length;
	}

}

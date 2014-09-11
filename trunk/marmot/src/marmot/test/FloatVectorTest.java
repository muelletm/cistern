package marmot.test;

import static org.junit.Assert.*;

import marmot.core.ConcatFloatFeatureVector;
import marmot.core.DenseArrayFloatFeatureVector;
import marmot.core.FloatFeatureVector;
import marmot.core.FloatWeights;
import marmot.core.ZeroFloatFeatureVector;

import org.junit.Test;

public class FloatVectorTest {

	class TestWeights implements FloatWeights {

		public int num_tags_;
		public double[] weights_;

		public TestWeights(int num_tags, int dim) {
			num_tags_ = num_tags;
			weights_ = new double[dim * num_tags];
		}

		@Override
		public int getFloatIndex(int feature, int tag_index) {
			return feature * num_tags_ + tag_index;
		}

		@Override
		public double getFloatWeight(int index) {
			return weights_[index];
		}

		@Override
		public void updateFloatWeight(int index, double value) {
			weights_[index] += value;
		}

	}

	public void sumHelper(double[] expected, TestWeights fw,
			FloatFeatureVector vc, double update) {
		assertArrayEquals(expected, fw.weights_, 1e-5);
		
		double expected_sum = 0.0;
		for (double f : expected) {
			expected_sum += f * f / update;
		}

		double actual_sum = vc.getDotProduct(fw, 0, 0);
		assertEquals(expected_sum, actual_sum, 1e-5);
	}

	@Test
	public void test() {

		{

			TestWeights fw = new TestWeights(1, 3 * 2);

			double[] w = { 1.0, 0.5, 0.3 };
			FloatFeatureVector v = new DenseArrayFloatFeatureVector(w);

			double[] w2 = { -1.0, -0.5, -0.3 };
			FloatFeatureVector v2 = new DenseArrayFloatFeatureVector(w2);

			FloatFeatureVector v3 = new ConcatFloatFeatureVector(v, v2);

			v3.updateFloatWeight(fw, 0, 0, 1.0);

			double[] expected = { 1.0, 0.5, 0.3, -1.0, -0.5, -0.3 };

			sumHelper(expected, fw, v3, 1.0);
		}

		{

			TestWeights fw = new TestWeights(1, 3 * 3);

			double[] w = { 1.0, 0.5, 0.3 };
			FloatFeatureVector v = new DenseArrayFloatFeatureVector(w);

			double[] w2 = { -1.0, -0.5, -0.3 };
			FloatFeatureVector v2 = new DenseArrayFloatFeatureVector(w2);

			double[] w3 = { -2.0, -0.7, -0.2 };
			FloatFeatureVector v3 = new DenseArrayFloatFeatureVector(w3);

			FloatFeatureVector vc = new ConcatFloatFeatureVector(v,
					new ConcatFloatFeatureVector(v2, v3));

			vc.updateFloatWeight(fw, 0, 0, 1.0);

			double[] expected = { 1.0, 0.5, 0.3, -1.0, -0.5, -0.3, -2.0, -0.7,
					-0.2 };

			

			sumHelper(expected, fw, vc, 1.0);

		}

	}

	@Test
	public void zero() {

		{

			TestWeights fw = new TestWeights(1, 3 * 2);

			FloatFeatureVector v = new ZeroFloatFeatureVector(3);

			double[] w2 = { -1.0, -0.5, -0.3 };
			FloatFeatureVector v2 = new DenseArrayFloatFeatureVector(w2);

			FloatFeatureVector v3 = new ConcatFloatFeatureVector(v, v2);

			v3.updateFloatWeight(fw, 0, 0, 1.0);

			double[] expected = { 0.0, 0.0, 0.0, -1.0, -0.5, -0.3 };

			sumHelper(expected, fw, v3, 1.0);

		}

		{

			TestWeights fw = new TestWeights(1, 3 * 2);

			double[] w = { 1.0, 0.5, 0.3 };
			FloatFeatureVector v = new DenseArrayFloatFeatureVector(w);

			FloatFeatureVector v2 = new ZeroFloatFeatureVector(3);

			FloatFeatureVector v3 = new ConcatFloatFeatureVector(v, v2);

			v3.updateFloatWeight(fw, 0, 0, 0.1);

			double[] expected = { .1, 0.05, 0.03, 0.0, 0.0, 0.0 };

			sumHelper(expected, fw, v3, 0.1);

		}

	}

}

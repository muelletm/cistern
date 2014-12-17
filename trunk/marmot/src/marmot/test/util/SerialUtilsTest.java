package marmot.test.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import marmot.util.SerialUtils;

import org.junit.Test;

public class SerialUtilsTest {

	void roundTripTest(double[] expected, double eps, double delta) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			SerialUtils.writeSparseArray(oos, expected, eps);
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		double[] actual;
		byte[] array = baos.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(array);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			actual = SerialUtils.readSparseArray(ois);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		assertArrayEquals(expected, actual, delta);
	}

	@Test
	public void trivialTest() {
		double eps = 1e-10;
		double delta = eps;
		double[] expected;
		expected = null;
		roundTripTest(expected, eps, delta);
		expected = new double[0];
		roundTripTest(expected, eps, delta);
	}

	@Test
	public void normalTest() {
		double eps = 1e-2;
		double delta = 1e-1;
		double[] expected = { 1.0, 0.0, 1.0, 0.0, 0.5, 0.11, 0.01, 0.001 };
		roundTripTest(expected, eps, delta);
	}

	@Test
	public void bigTest() {
		double[] expected = new double[10000000];
		Random random = new Random(42L);
		for (int i = 0; i < expected.length; i++) {
			expected[i] = random.nextGaussian();
		}
		double eps = 1e-2;
		double delta = 1e-1;
		roundTripTest(expected, eps, delta);
	}
	
	@Test
	public void zeroOffsetTest() {
		double[] expected = {1.0, 0.0, 0.0, 0.0};
		double eps = 1e-2;
		double delta = 1e-1;
		roundTripTest(expected, eps, delta);
	}
	
	@Test
	public void bigOffsetTest() {
		double[] expected = {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		double eps = 1e-2;
		double delta = 1e-1;
		roundTripTest(expected, eps, delta);
	}

}

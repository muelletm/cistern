// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

public class Numerics {

	
	public final static double EPSILON = 1e-5;

	/**
	 * Determines whether two 1d jagged arrays are approximately equal 	
	 * @param array1
	 * @param array2
	 * @param eps
	 * @return
	 */
	public static boolean approximatelyEqual(double[] array1, double[] array2, double eps) {
		boolean approxEqual = true;
		for (int i = 0; i < array1.length; ++i) {
			if (Math.abs(array1[i] - array2[i]) > eps) {
				approxEqual = false;
				break;
			}		
		}
		return approxEqual;
	}
	
	/**
	 * Determines whether two 2d jagged arrays are approximately equal 	
	 * @param array1
	 * @param array2
	 * @param eps
	 * @return
	 */
	public static boolean approximatelyEqual(double[][] array1, double[][] array2, double eps) {
		boolean approxEqual = true;
		for (int i = 0; i < array1.length; ++i) {
			for (int j = 0; j < array1[i].length; ++j) {
				if (Math.abs(array1[i][j] - array2[i][j]) > eps) {
					approxEqual = false;
					break;
				}		
			}
		}
		return approxEqual;
	}
	
	/**
	 * Determines whether two 3d jagged arrays are approximately equal 	
	 * @param array1
	 * @param array2
	 * @param eps
	 * @return
	 */
	public static boolean approximatelyEqual(double[][][] array1, double[][][] array2, double eps) {
		boolean approxEqual = true;
		for (int i = 0; i < array1.length; ++i) {
			for (int j = 0; j < array1[i].length; ++j) {
				for (int k = 0; k < array1[i][j].length; ++k) {
					if (Math.abs(array1[i][j][k] - array2[i][j][k]) > eps) {
						approxEqual = false;
						break;
					}
				}
			}
		}
		
		return approxEqual;
	}
	
	// The following code is based on a similar function in MALLET.
	// (http://www.cs.umass.edu/~mccallum/mallet)
	// But was modified using the thresholds from log1pexp.
	public static double sumLogProb(double a, double b) {
		if (a == Double.NEGATIVE_INFINITY) {
			if (b == Double.NEGATIVE_INFINITY)
				return Double.NEGATIVE_INFINITY;
			return b;
		}

		if (b == Double.NEGATIVE_INFINITY) {
			return a;
		}

		if (a > b) {
			return a + Math.log1p(Math.exp(b - a));
		}

		return b + Math.log1p(Math.exp(a - b));
	}

	// Adapted from http://web.science.mq.edu.au/~mjohnson/code/digamma.c
	// Written by Mark Johnson
	public static double digamma(double x) {

		double result, xx, xx2, xx4;

		if (x == 0.) {
			return 0;
		} else if (x < 0.) {
			throw new IllegalArgumentException("x <= 0 : x =" + x);
		}

		result = 0;
		for (; x < 7; ++x) {
			result -= 1 / x;
		}

		x -= 0.5;
		xx = 1.0 / x;
		xx2 = xx * xx;
		xx4 = xx2 * xx2;
		result += Math.log(x) + (1. / 24.) * xx2 - (7.0 / 960.0) * xx4
				+ (31.0 / 8064.0) * xx4 * xx2 - (127.0 / 30720.0) * xx4 * xx4;

		return result;
	}

	public static double exp_digamma(double x) {
		return Math.exp(digamma(x));
	}

	public static boolean approximatelyGreaterEqual(double a,
			double b, double epsilon) {
		return a + epsilon > b;
	}
	
	public static boolean approximatelyGreaterEqual(double a,
			double b) {
		return approximatelyGreaterEqual(a, b, EPSILON);
	}

}

// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.util;

public class Numerics {

	// Following code is taken from Mallet (http://www.cs.umass.edu/~mccallum/mallet) 
	public static double sumLogProb (double a, double b)
	{
		if (a == Double.NEGATIVE_INFINITY) {
			if (b == Double.NEGATIVE_INFINITY)
				return Double.NEGATIVE_INFINITY;
      return b;
		}
		else if (b == Double.NEGATIVE_INFINITY)
			return a;
		else if (a > b)
			return a + Math.log (1 + Math.exp(b-a));
		else
			return b + Math.log (1 + Math.exp(a-b));
	}
	
	// Adapted from http://web.science.mq.edu.au/~mjohnson/code/digamma.c
	// Written by Mark Johnson
	public static double digamma(double x) {
		
		double result, xx, xx2, xx4;
		
		if (x == 0.){
			return 0;
		}
		else if (x < 0.){
			throw new IllegalArgumentException("x <= 0 : x ="+x); 
		}
				
		result = 0;
		for ( ; x < 7; ++x){
			result -= 1/x;
		}
		 
		x -= 0.5;
		xx = 1.0/x;
		xx2 = xx*xx;
		xx4 = xx2*xx2;
		result += Math.log(x)
				+(1./24.)*xx2
				-(7.0/960.0)*xx4
				+(31.0/8064.0)*xx4*xx2
				-(127.0/30720.0)*xx4*xx4;
		
		return result;
	}
	
	public static double exp_digamma(double x){
		return Math.exp(digamma(x));
	}
}

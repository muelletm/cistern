// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

public class Check {

	public static boolean isNormal(double d) {
		return !(Double.isInfinite(d) || Double.isNaN(d));
	}
	
}

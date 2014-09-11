package marmot.util;

public class Check {

	public static boolean isNormal(double d) {
		return !(Double.isInfinite(d) || Double.isNaN(d));
	}
	
}

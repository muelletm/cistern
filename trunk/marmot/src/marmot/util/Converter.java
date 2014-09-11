package marmot.util;

import java.util.List;

public class Converter {

	public static int[] toIntArray(List<Integer> list) {
		if (list == null) {
			return null;
		}
		
		int[] array = new int[list.size()];
		int index = 0;
		for (int i : list) {
			array[index++] = i;
		}
		return array;
	}
	
	public static double[] toDoubleArray(List<Double> list) {
		if (list == null) {
			return null;
		}
		
		double[] array = new double[list.size()];
		int index = 0;
		for (double i : list) {
			array[index++] = i;
		}
		return array;
	}
	
	public static String[] toStringArray(List<String> list) {
		if (list == null) {
			return null;
		}
		
		String[] array = new String[list.size()];
		int index = 0;
		for (String i : list) {
			array[index++] = i;
		}
		return array;
	}
	
}

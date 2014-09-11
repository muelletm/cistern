// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.security.InvalidParameterException;

public class StringUtils {

	public static double[] parseDoubleArray(String array_string, Mutable<Integer> start_index) {
		String[] element_strings = parseArray(array_string, start_index);

		double[] array = new double[element_strings.length];

		for (int index = 0; index < element_strings.length; index++) {
			double element = Double.valueOf(element_strings[index]);
			array[index] = element;
		}

		return array;
	}

	public static String[] parseArray(String array_string, Mutable<Integer> index) {
		int start_index = array_string.indexOf('[', index.get());
		int end_index = array_string.indexOf(']', start_index);
		
		if (start_index == -1 || end_index == -1) {
			throw new InvalidParameterException("Not an array: " + array_string);			
		}
		
		array_string = array_string.substring(start_index + 1, end_index);
		index.set(end_index + 1);
		
		if (array_string.length() == 0) {
			return new String[0];
		}
		
		return array_string.split(",");
	}

	public static String reverse(String form) {
		return new StringBuilder(form).reverse().toString();
	}
	
	public static String normalize(String word, boolean replace_umlaut) {
		StringBuilder sb = new StringBuilder(word.length());
		for (int index = 0; index < word.length(); index++) {
			char c = Character.toLowerCase(word.charAt(index));
			if (Character.isDigit(c)) {
				sb.append('0');
			} else {

				if (replace_umlaut) {

					switch (c) {
					case 'ß':
						sb.append("ss");
						break;
					case 'ö':
						sb.append("oe");
						break;
					case 'ü':
						sb.append("ue");
						break;
					case 'ä':
						sb.append("ae");
						break;
					default:
						sb.append(c);
					}

				} else {
					sb.append(c);
				}

			}
		}
		return sb.toString();
	}

}

// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.security.InvalidParameterException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {

	public enum Mode {
		none, bracket, lower, umlaut,
	}

	public static double[] parseDoubleArray(String array_string,
			Mutable<Integer> start_index) {
		String[] element_strings = parseArray(array_string, start_index);

		double[] array = new double[element_strings.length];

		for (int index = 0; index < element_strings.length; index++) {
			double element = Double.valueOf(element_strings[index]);
			array[index] = element;
		}

		return array;
	}

	public static String[] parseArray(String array_string,
			Mutable<Integer> index) {
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

	static final Map<String, Character> BRACKET_MAP = new HashMap<String, Character>();
	static {
		BRACKET_MAP.put("-LRB-", '(');
		BRACKET_MAP.put("-RRB-", ')');
		BRACKET_MAP.put("-LCB-", '{');
		BRACKET_MAP.put("-RCB-", '}');
		BRACKET_MAP.put("-LSB-", '[');
		BRACKET_MAP.put("-RSB-", ']');
	}

	public static String normalize(String word, Mode mode) {
		if (mode == null || mode == Mode.none) {
			return word;
		}

		StringBuilder sb = new StringBuilder(word.length());
		int index = 0;
		while (index < word.length()) {
			char c = word.charAt(index);

			if (c == '-' && index + 4 < word.length()) {
				String bracket_string = word.substring(index, index + 5);
				Character bracket_char = BRACKET_MAP.get(bracket_string);
				if (bracket_char != null) {
					c = bracket_char;
					index += 4;
				}
			}

			if (mode == Mode.lower || mode == Mode.umlaut) {
				c = Character.toLowerCase(c);

				if (Character.isDigit(c)) {
					c = '0';
				}

			}

			if (mode == Mode.umlaut) {

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

			index++;
		}
		return sb.toString();
	}

	public static String join(List<String> segments) {
		StringBuilder sb = new StringBuilder();
		for (String segment : segments) {
			sb.append(segment);
		}
		return sb.toString();
	}

	public static String clean(String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (Character.isWhitespace(c) || c == 160) {
				c = ' ';
			}
			sb.append(c);
		}

		return sb.toString();
	}

	public static boolean containsUpperCase(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isUpperCase(word.charAt(i)))
				return true;
		}
		return false;
	}

	public static boolean containsLowerCase(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isLowerCase(word.charAt(i)))
				return true;
		}
		return false;
	}

	public static boolean containsDigit(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isDigit(word.charAt(i)))
				return true;
		}
		return false;
	}

	public static boolean containsHyphon(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == '-')
				return true;
		}
		return false;
	}

	public static boolean containsSpecial(String word) {
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (isSpecial(c))
				return true;
		}
		return false;
	}

	private static boolean isSpecial(char c) {
		return !(Character.isLetter(c) || Character.isDigit(c));
	}

	public static String asciify(String form) {
		return Normalizer.normalize(form, Normalizer.Form.NFD).replaceAll(
				"[^\\p{ASCII}]", "");
	}
	
	public static enum Shape {
		FirstCap, AllCap, Lower, Mixed, NoLetter;
	}

	public static Shape getShape(String word) {
		int num_lower = 0;
		int num_letter = 0;
		int num_upper = 0;
		
		for (int i=0; i<word.length(); i++) {
			char c = word.charAt(i);
			if (Character.isLetter(c)) {
				num_letter ++;
				if (Character.isLowerCase(c)) {
					num_lower ++;
				}
				if (Character.isUpperCase(c)) {
					num_upper ++;
				}
			}
		}
		
		if (num_letter == 0) {
			return Shape.NoLetter;
		}
		
		if (num_lower == 0) {
			return Shape.AllCap;
		}
		
		boolean first_cap = Character.isUpperCase(word.charAt(0));
		if (first_cap && num_upper == 1) {
			return Shape.FirstCap;
		}
		
		if (num_upper > 0) {
			return Shape.Mixed;
		}

		return Shape.Lower;
	}

	public static String capitalize(String word) {
		StringBuilder sb = new StringBuilder(word);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	public static boolean containsLetter(String word) {
		for (int i=0; i<word.length(); i++) {
			char c = word.charAt(i);
			if (Character.isLetter(c)) {
				return true;
			}
		}
		return false;
	}

}

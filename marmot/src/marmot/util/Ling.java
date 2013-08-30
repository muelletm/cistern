// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

public class Ling {

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

}

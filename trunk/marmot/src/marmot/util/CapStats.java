// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

public class CapStats {

	public enum CapType {
		lower, upper, allcaps, other;

		public static String toString(CapType cap_type) {
			if (cap_type == null) {
				return "null";
			}
			return cap_type.toString();
		}
	};
	
	public static CapType getCapType(String form) {

		boolean has_letter = false;
		boolean has_lower = false;
		boolean has_upper = false;
		boolean has_none_first_upper = false;

		for (int i = 0; i < form.length(); i++) {
			char c = form.charAt(i);

			if (Character.isLetter(c)) {
				has_letter = true;

				if (Character.isUpperCase(c)) {
					has_upper = true;
					if (i > 0) {
						has_none_first_upper = true;
					}
				} else {
					has_lower = true;
				}

			}

		}

		if (!has_letter) {
			return null;
		}

		if (!has_upper) {
			return CapType.lower;
		}

		if (!has_none_first_upper) {
			return CapType.upper;
		}

		if (!has_lower) {
			return CapType.allcaps;
		}

		return CapType.other;
	}

}

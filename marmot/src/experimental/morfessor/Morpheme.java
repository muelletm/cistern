// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package experimental.morfessor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Morpheme {
	public final static String NON_MORPHEME = "ZZZ";
	public final static String STEM = "STM";
	private String morpheme_;
	private String asterisk_;
	private String tag_;

	public Morpheme(String morpheme, String asterisk, String tag) {
		morpheme_ = morpheme;
		asterisk_ = asterisk;
		tag_ = tag;
	}

	private static final Pattern MORPHEME_ASTERISK_TAG_PATTERN_ = Pattern
			.compile("^([^*]*)(\\*[0-4])?(/[A-Z]*)$");

	public Morpheme(String morpheme_asterisk_tag) {
		Matcher m = MORPHEME_ASTERISK_TAG_PATTERN_.matcher(morpheme_asterisk_tag);
		
		if (!m.matches()) {
			throw new InvalidParameterException();
		}
		
		morpheme_ = m.group(1);	
		asterisk_ = m.group(2);
		if (asterisk_ != null) {
			asterisk_ = new String(asterisk_.substring(1));
		}
		tag_ = new String(m.group(3).substring(1));
	}
	
	public static String join(List<Morpheme> morphemes, boolean with_asterisk, boolean with_tag, String seperator) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (Morpheme morph : morphemes) {
			sb.append(morph.toString(with_asterisk, with_tag));
			if (index < morphemes.size() - 1) {
				sb.append(seperator);
			}
			index ++;
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toString(true, true);
	}
	
	public String toString(boolean with_asterisk, boolean with_tag) {
		StringBuilder sb = new StringBuilder(morpheme_);
				
		if (with_asterisk && (asterisk_ != null)) {
			sb.append("*" + asterisk_);
		}
		
		if (with_tag) {
			sb.append("/" + tag_);
		}
		
		return sb.toString();
	}
	
	public static List<Morpheme> split(String morphemes) {
		String[] tokens = morphemes.split(" \\+ ");
		List<Morpheme> morpheme_list = new ArrayList<Morpheme>(tokens.length);
		for (String token : tokens) {
			morpheme_list.add(new Morpheme(token));
		}
		return morpheme_list;
	}

	public String getTag() {
		return tag_;
	}

	public String getMorphAsterisk() {
		return toString(true, false);
	}

	public boolean isNonMorpheme() {
		return tag_.equals(NON_MORPHEME);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof Morpheme)) {
			return false;
		}
		
		Morpheme morph = (Morpheme) other;
		
		if (!morph.morpheme_.equals(morpheme_)) {
			return false;
		}
		
		if (!morph.tag_.equals(tag_)) {
			return false;
		}
		
		return true;
	}

	public void setTag(String tag) {
		tag_ = tag;
	}

	public String getMorpheme() {
		return morpheme_;
	}

	public void setMorpheme(String morpheme) {
		morpheme_ = morpheme;
	}

	public void setAsterisk(String asterisk) {
		asterisk_ = asterisk;
	} 
}

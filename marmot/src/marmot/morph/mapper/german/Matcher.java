// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.morph.mapper.german;

public class Matcher {

	boolean value_;
	int matches_;

	public Matcher() {
		value_ = true;
	}

	public boolean matches() {
		return value_;
	}

	public void add(String string, String string2) {
		if (string2.equals("_")) {
			return;
		}
		
		//if (string.equals("amb")) {
		//	return;
		//}
		
		if (!string.equals(string2)) {
			value_ = false;
		}
	}

}

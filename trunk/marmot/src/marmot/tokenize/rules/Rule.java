// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.util.regex.Pattern;

public class Rule {

	public Pattern pattern;
	public String replacement;

	public Rule(Pattern pattern, String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
	}

}

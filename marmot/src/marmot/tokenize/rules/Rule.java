// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Rule implements Serializable {

	private static final long serialVersionUID = 1L;
	public Pattern pattern;
	public String replacement;

	public Rule(Pattern pattern, String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
	}

}

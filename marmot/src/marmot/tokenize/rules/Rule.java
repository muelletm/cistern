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

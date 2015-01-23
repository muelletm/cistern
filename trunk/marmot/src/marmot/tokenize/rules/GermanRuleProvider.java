// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class GermanRuleProvider extends RuleProvider {

	@Override
	public Collection<Rule> getTokRules() {
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(new Rule(Pattern.compile("(ÿ)") ,"")); // annotation error..
		rules.add(new Rule(Pattern.compile("(\\. \\.)") ,"."));
		return rules;
	}

	@Override
	public Collection<Rule> getUnTokRules() {
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(new Rule(Pattern.compile("(ð)") ,"")); // annotation error..
		rules.add(new Rule(Pattern.compile("(>)") ,"")); // annotation error..
		return rules;
	}

}

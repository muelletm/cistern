// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CzechRuleProvider extends RuleProvider {

	@Override
	public Collection<Rule> getTokRules() {
		List<Rule> rules = new LinkedList<Rule>();	
		addSimpleRule("&quot;", "\"", rules);
		addSimpleRule("&apos;", "'", rules);
		//addSimpleRule(" &#8206;", "", rules);
		//addSimpleRule(" 8206#&", "", rules);
		//addSimpleRule(" #8206&", "", rules);
		return rules;
	}

	@Override
	public Collection<Rule> getUnTokRules() {
		List<Rule> rules = new LinkedList<Rule>();

		return rules;
	}

}

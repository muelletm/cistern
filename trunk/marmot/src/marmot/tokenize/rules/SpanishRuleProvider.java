// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class SpanishRuleProvider extends RuleProvider {

	@Override
	public Collection<Rule> getTokRules() {
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(new Rule(Pattern.compile("\\S( Fz)") ,""));
		rules.add(new Rule(Pattern.compile("\\S(_)\\S") ," "));
		rules.add(new Rule(Pattern.compile("( ̃)") , ""));
		return rules;
	}

	@Override
	public Collection<Rule> getUnTokRules() {
		List<Rule> rules = new LinkedList<Rule>();	
		addSimpleRule("del", "de el", rules);
		addSimpleRule("al", "a el", rules); // Al-razir?
		//rules.add(new Rule(Pattern.compile("\\w+(dole)\\W") , "(do le)"));
		//rules.add(new Rule(Pattern.compile("\\w+(dose)\\W") , "(do se)"));
		// TODO: llamarse, ponerse, pongome, ... how many? Maybe make a list?
		// Regex might lead to too many errors
		return rules;
	}

}

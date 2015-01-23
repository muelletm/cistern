// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class EnglishRuleProvider extends RuleProvider {

	@Override
	public Collection<Rule> getTokRules() {
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(new Rule(Pattern.compile("(``)") ,"\""));
		rules.add(new Rule(Pattern.compile("(`)") ,"'"));
		rules.add(new Rule(Pattern.compile("('')") ,"\""));
		rules.add(new Rule(Pattern.compile("(--)") ,"-")); 
		rules.add(new Rule(Pattern.compile("(\\.\\.\\.)") ,"…"));
		rules.add(new Rule(Pattern.compile("(\\. \\.)$") ,"."));		
		
		return rules;
	}

	@Override
	public Collection<Rule> getUnTokRules() {
		List<Rule> rules = new LinkedList<Rule>();	
		rules.add(new Rule(Pattern.compile("(\\()") ,"-LRB-"));
		rules.add(new Rule(Pattern.compile("(\\))") ,"-RRB-"));
		rules.add(new Rule(Pattern.compile("(\\{)") ,"-LCB-"));
		rules.add(new Rule(Pattern.compile("(\\})") ,"-RCB-"));
		rules.add(new Rule(Pattern.compile("(\\[)") ,"-LSB-"));
		rules.add(new Rule(Pattern.compile("(\\])") ,"-RSB-"));
		rules.add(new Rule(Pattern.compile("(¼)") ,"1/4"));
		rules.add(new Rule(Pattern.compile("(\\. ?\\.)$") ,"."));

		return rules;
	}

}

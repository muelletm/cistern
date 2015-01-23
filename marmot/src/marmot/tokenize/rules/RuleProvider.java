// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public abstract class RuleProvider {

	abstract public Collection<Rule> getTokRules();
	abstract public Collection<Rule> getUnTokRules();
	
	public static RuleProvider createRuleProvider(String lang) {
		
		if (lang.equalsIgnoreCase("es")) {
			return new SpanishRuleProvider();
		}
		if (lang.equalsIgnoreCase("en")) {
			return new EnglishRuleProvider();
		}
		if (lang.equalsIgnoreCase("de")) {
			return new GermanRuleProvider();
		}
		
		return null;
	}
	
	public void addSimpleRule(String a, String b, List<Rule> rules) {
		rules.add(new Rule(
				Pattern.compile("\\P{L}("+a+")\\P{L}", Pattern.CASE_INSENSITIVE), 
				b
		));
		rules.add(new Rule(
				Pattern.compile("^("+a+")\\P{L}", Pattern.CASE_INSENSITIVE), 
				b
		));
		rules.add(new Rule(
				Pattern.compile("\\P{L}("+a+")$", Pattern.CASE_INSENSITIVE), 
				b
		));
		rules.add(new Rule(
				Pattern.compile("^("+a+")$", Pattern.CASE_INSENSITIVE), 
				b
		));
	}
	
	public RulebasedTransformator getTokTransformator() {
		
		if (getTokRules() == null) {
			return null;
		}
		
		return new RulebasedTransformator(getTokRules());
	}
	
	public RulebasedTransformator getUnTokTransformator() {
		
		if (getUnTokRules() == null) {
			return null;
		}
		
		return new RulebasedTransformator(getUnTokRules());
	}
	
}

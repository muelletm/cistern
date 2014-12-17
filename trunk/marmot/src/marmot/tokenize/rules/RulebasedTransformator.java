// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.tokenize.rules;

import java.util.Collection;
import java.util.regex.Matcher;

public class RulebasedTransformator {
	/**
	 * This class can define a number of conversion rules for exceptions which arise in either
	 * direction of tokenization. After finding a match in the corresponding string, a 
	 * confirmation match is searched for in the other one. Apart from confirming, little can
	 * be done though, since this code is run strictly before alignment, so we don't know
	 * anything about corresponding positions between the two strings.
	 * After confirmation, the initial match is replaced with the confirmation match.
	 */
	private Collection<Rule> rules_;
		
	public RulebasedTransformator(Collection<Rule> rules) {
		rules_ = rules;
	}

	public String applyRules(String string) {
		return applyRules(string, rules_);
	}
	
	private String applyRules(String string, Collection<Rule> rules) {
		StringBuilder sb = new StringBuilder();
		
		for(Rule rule : rules) {
			Matcher matcher = rule.pattern.matcher(string);

			int end = 0;
			while(matcher.find()) {			
				int start = matcher.start(1);
				sb.append(string.substring(end, start));
				sb.append(rule.replacement);
				end   = matcher.end(1);
			}
			sb.append(string.substring(end));
			
			string = sb.toString();
			// Reset string builder.
			sb.setLength(0);
		}	
		return string;
	}
}
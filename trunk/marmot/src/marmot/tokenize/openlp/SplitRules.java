package marmot.tokenize.openlp;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import marmot.tokenize.rules.RuleProvider;

public class SplitRules {
	/**
	 * This class can define a number of conversion rules for exceptions which arise in either
	 * direction of tokenization. After finding a match in the corresponding string, a 
	 * confirmation match is searched for in the other one. Apart from confirming, little can
	 * be done though, since this code is run strictly before alignment, so we don't know
	 * anything about corresponding positions between the two strings.
	 * After confirmation, the initial match is replaced with the confirmation match.
	 */
	private Map<Matcher, Matcher> tok_rules_;
	private Map<Matcher, Matcher> untok_rules_;
	
	public SplitRules() {
		this(RuleProvider.createRuleProvider("es"));		
	}
	
	public SplitRules(RuleProvider rule_provider) {
		this(rule_provider.getTokRules(), rule_provider.getUnTokRules());
	}
	
	public SplitRules(Map<Matcher, Matcher> tok_rules, Map<Matcher, Matcher> untok_rules) {
		tok_rules_ = tok_rules;
		untok_rules_ = untok_rules;
	}

	public String[] applyRules(String untok, String tok) {
		// calls the rule sets for both directions and sends the results back
		String[] result = applyRule(tok, untok, tok_rules_);
		tok = result[0];
		untok = result[1];
		return applyRule(untok, tok, untok_rules_);
	}
	
	public static void addRule(String a, String b, Map<Matcher, Matcher> rule_book) {
		// internal encapsulation of the rule-adding routine. Note that it's private 
		rule_book.put(
				Pattern.compile("\\W"+a+"\\W", Pattern.CASE_INSENSITIVE).matcher(""), 
				Pattern.compile("\\W"+b+"\\W", Pattern.CASE_INSENSITIVE).matcher("")
		);
	}
	
	private String[] applyRule(String a, String b, Map<Matcher, Matcher> rule_book) {
		// application of either of the two rule sets
		for(Matcher key : rule_book.keySet()) {
			Matcher value = rule_book.get(key);
			String replace_string = value.pattern().pattern();
			replace_string = replace_string.substring(2, replace_string.length()-2);
			int a_occurrence = 0;
			int b_occurrence = 0;
			while(key.reset(a).find(a_occurrence) && value.reset(b).find(b_occurrence)) {			
				a_occurrence = key.start()+1;
				b_occurrence = value.start()+1;				
				a = a.substring(0, a_occurrence) + replace_string + a.substring(key.end()-1);
			}
		}
		String[] ret = {a, b};
		return ret;
	}
}
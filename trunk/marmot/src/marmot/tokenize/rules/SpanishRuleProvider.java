package marmot.tokenize.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import marmot.tokenize.openlp.SplitRules;

public class SpanishRuleProvider extends RuleProvider {

	@Override
	public Map<Matcher, Matcher> getTokRules() {
		Map<Matcher, Matcher> tok_rules = new HashMap<Matcher, Matcher>();
		SplitRules.addRule("Fz", "", tok_rules);
		return tok_rules;
	}

	@Override
	public Map<Matcher, Matcher> getUnTokRules() {
		Map<Matcher, Matcher> untok_rules = new HashMap<Matcher, Matcher>();
		SplitRules.addRule("del", "de el", untok_rules);
		SplitRules.addRule("al", "a el", untok_rules);
		return untok_rules;
	}

	

}

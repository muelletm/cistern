package marmot.tokenize.rules;

import java.util.Map;
import java.util.regex.Matcher;

public abstract class RuleProvider {

	abstract public Map<Matcher, Matcher> getTokRules();
	abstract public Map<Matcher, Matcher> getUnTokRules();
	
	public static RuleProvider createRuleProvider(String lang) {
		
		if (lang.equalsIgnoreCase("es")) {
			return new SpanishRuleProvider();
		}
		
		throw new RuntimeException("Unsupported langauge: " + lang);
	}
	
}

package marmot.tokenize.openlp;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitRules {
	private Map<Matcher, Matcher> tok_rules_;
	private Map<Matcher, Matcher> untok_rules_;
	
	public SplitRules() {
		tok_rules_ = new HashMap<Matcher, Matcher>();
		untok_rules_ = new HashMap<Matcher, Matcher>();
		addRule("del", "de el", untok_rules_);
		addRule("al", "a el", untok_rules_);
		addRule("Fz", "", tok_rules_);
	}
	
	public String[] applyRules(String untok, String tok) {
		String[] result = applyRule(tok, untok, tok_rules_);
		tok = result[0];
		untok = result[1];
		return applyRule(untok, tok, untok_rules_);
	}
	
	private void addRule(String a, String b, Map<Matcher, Matcher> rule_book) {
		rule_book.put(
				Pattern.compile("\\W"+a+"\\W", Pattern.CASE_INSENSITIVE).matcher(""), 
				Pattern.compile("\\W"+b+"\\W", Pattern.CASE_INSENSITIVE).matcher("")
		);
	}
	
	
	
	private String[] applyRule(String a, String b, Map<Matcher, Matcher> rule_book) {
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
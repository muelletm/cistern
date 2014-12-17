package marmot.test;

import static org.junit.Assert.*;

import marmot.tokenize.openlp.SplitRules;
import marmot.tokenize.rules.RuleProvider;

import org.junit.Test;

public class SplitRulesTest {

	public void test(String lang, String untok, String tok, String expected_untok, String expected_tok){
		SplitRules r = new SplitRules(RuleProvider.createRuleProvider(lang));
		
		String[] result = r.applyRules(untok, tok);
		
		String actual_untok = result[0];
		String actual_tok = result[1];
		
		assertEquals(expected_untok, actual_untok);
		assertEquals(expected_tok, actual_tok);
	} 
	
	@Test
	public void test() {
		// Fails:
		//test("del", "de el", "de el", "de el");
		
		test("es", " del ", " de el ", " de el ", " de el ");
		test("es"," del. ", " de el .", " de el. ", " de el ." );
		test("es"," adela del ", " adela de el ", " adela de el ", " adela de el ");
		
	}

}

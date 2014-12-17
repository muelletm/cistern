// Copyright 2014 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.test.tokenizer.rules;

import static org.junit.Assert.*;
import marmot.tokenize.rules.RuleProvider;
import marmot.tokenize.rules.RulebasedTransformator;

import org.junit.Test;

public class RulebasedTransformatorTest {

	public void testTokRules(String lang, String input, String expected){
		RuleProvider p = RuleProvider.createRuleProvider(lang);
		RulebasedTransformator r = p.getTokTransformator();		
		test(r, input, expected);
	}
	
	public void testUnTokRules(String lang, String input, String expected){
		RuleProvider p = RuleProvider.createRuleProvider(lang);
		RulebasedTransformator r = p.getUnTokTransformator();		
		test(r, input, expected);
	} 

	private void test(RulebasedTransformator r, String input, String expected) {
		String actual = r.applyRules(input);
		assertEquals(expected, actual);
	}

	@Test
	public void testSpanishTokRules() {
		testUnTokRules("es", "no match.", "no match.");
		testUnTokRules("es"," del. ", " de el. ");
		
		testUnTokRules("es"," adela del ", " adela de el ");
		testUnTokRules("es"," ádela del ", " ádela de el ");
		testUnTokRules("es"," ödela del ", " ödela de el ");
		testUnTokRules("es"," ñdela del ", " ñdela de el ");
		
		testUnTokRules("es", " del ", " de el ");
		testUnTokRules("es", "del", "de el");
		testUnTokRules("es", " del", " de el");
		testUnTokRules("es", "del ", "de el ");
	}
	
	@Test 
	public void testSpanishUnTokRules() {
		testTokRules("es", "dejando_de_lado", "dejando de lado");		
		testTokRules("es", " 1.0 Fz ", " 1.0 ");
		testTokRules("es", " 1.0 FZ ", " 1.0 FZ ");
		testTokRules("es", " 1.0 Fz", " 1.0");	
	}

}
